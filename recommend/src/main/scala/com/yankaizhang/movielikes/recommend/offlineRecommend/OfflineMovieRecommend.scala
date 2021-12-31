package com.yankaizhang.movielikes.recommend.offlineRecommend

import com.yankaizhang.movielikes.recommend.constant.OfflineConstants
import com.yankaizhang.movielikes.recommend.entity.Similarity
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel


/**
 * 基于物品的离线协同过滤
 */
object OfflineMovieRecommend {

  def main(args: Array[String]): Unit = {

    // 1. 创建sparkSession
    val uri = OfflineConstants.MONGO_DB_HOST + OfflineConstants.SPARK_MONGO_OUTPUT + "." + OfflineConstants.OFFLINE_RECOMMEND_OUTPUT;
    val sparkSession = SparkSession.builder()
      .config("spark.mongodb.output.uri", uri)
      .getOrCreate()
    sparkSession.sparkContext.setLogLevel("WARN")
    import sparkSession.implicits._

    // 2. 加载相似度矩阵
    val sim = sparkSession.read
      .format("mongo")
      .option("uri", OfflineConstants.MONGO_DB_HOST + OfflineConstants.SPARK_MONGO_OUTPUT)
      .option("collection", OfflineConstants.SIM_MEASURE_MAP(OfflineConstants.SIM_MEASURE_CHOICE))
      .load()
      .as[Similarity]
      .rdd
      .map(entity => (entity.movieId1, entity.movieId2, entity.similarity))
      .toDF("movieId1", "movieId2", "similarity")
      .persist(StorageLevel.MEMORY_ONLY_SER)

    /*
    db.ratings.aggregate([
            {$sort : {userId: 1, rating: -1}},
            {
                $group : {
                _id: "$userId",
                movie_list: {
                    $push: {movieId: "$movieId", rating: "$rating"}}
                }
            },
            {$project: {_id: 0, userId: "$_id", movie_list: {$slice: ["$movie_list", 0, 10]}}},
            {$unwind: "$movie_list"},
            {$project: {_id: 0, userId: 1, movieId: "$movie_list.movieId", rating: "$movie_list.rating"}},
            {$out: "rated_movies"}
        ],
        {allowDiskUse: true}
    )
    */

    // 3. 加载用户主动评分的电影
    val userInterestDF = sparkSession.read
      .format("mongo")
      .option("uri", OfflineConstants.MONGO_DB_HOST + OfflineConstants.MOVIELENS_COLLECTION_NAME)
      .option("collection", "rated_movies")
      .load()
      .map(row => (row.getAs[Int]("userId"), row.getAs[Int]("movieId"), row.getAs[Double]("rating")))
      .toDF("userId", "movieId", "rating")
      .persist(StorageLevel.MEMORY_ONLY_SER)


    // 4. 根据用户感兴趣的电影，和TopK召回相似物品矩阵中的电影做笛卡尔积，计算相似的电影评分乘积和
    userInterestDF.join(sim, $"movieId" <=> sim("movieId1"))
      .selectExpr("userId"
        , "movieId1 as relatedItem"
        , "movieId2 as otherItem"
        , "similarity"
        , "similarity * rating as simProduct")
      .createOrReplaceTempView("tempTable")


    // 5. 为每个用户推荐电影
    val recommendResultDF: DataFrame = sparkSession.sql(
      """|SELECT userId
         |,  otherItem
         |,  sum(simProduct) as rating
         |FROM tempTable
         |GROUP BY userId, otherItem
         |ORDER BY rating desc
      """.stripMargin)
      .rdd
      .mapPartitions(part => {
        part.map(row => (row.getInt(0), row.getInt(1)))
      })
      .groupByKey()
      .map {
        case (userId, iterable) =>
          (userId, iterable.toList.take(OfflineConstants.RECOMMEND_COUNT_PER))
      }
      .toDF("userId", "recommendations")

    // 6. 推荐结果写入MongoDB
    import com.mongodb.spark._
    MongoSpark.save(recommendResultDF.write.mode("overwrite"))

    sparkSession.stop()
  }
}