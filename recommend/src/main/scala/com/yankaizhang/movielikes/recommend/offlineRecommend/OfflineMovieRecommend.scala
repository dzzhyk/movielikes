package com.yankaizhang.movielikes.recommend.offlineRecommend

import com.yankaizhang.movielikes.recommend.entity.{Similarity, UserRecommendation}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel
import com.yankaizhang.movielikes.recommend.constant.SimilarityMeasureConstant.{COS_SIM, IMP_COS_SIM, CO_OCCUR_SIM}


/**
 * 基于物品的离线协同过滤
 */
object OfflineMovieRecommend {

  // 是否为大数据
  val isBig = true

  val SimMatrix = Map(
    COS_SIM -> (if (isBig) "itemCF_cosSim_simMatrix_big" else "itemCF_cosSim_simMatrix"),
    IMP_COS_SIM -> (if (isBig) "itemCF_impCosSim_simMatrix_big" else "itemCF_impCosSim_simMatrix"),
    CO_OCCUR_SIM -> (if (isBig) "itemCF_coOccurSim_simMatrix_big" else "itemCF_coOccurSim_simMatrix")
  )

  // 选择的相似度
  val SIM_MEASURE_CHOICE: String = IMP_COS_SIM

  // 数据集uri
  val DATA_MONGO_URI: String = (if (isBig) "mongodb://192.168.0.100:27017/movie_recommend_big" else "mongodb://192.168.0.100:27017/movie_recommend")

  // 电影相似度存储uri
  val SIM_MATRIX_OUTPUT_URI: String = "mongodb://192.168.0.100:27017/spark_output"

  // 推荐结果存储uri
  val RECOMMEND_OUTPUT_URI: String = (if (isBig) "mongodb://192.168.0.100:27017/spark_output.itemCF_result_big" else "mongodb://192.168.0.100:27017/spark_output.itemCF_result")

  // 并行度
  val defaultParallelism = 960

  def main(args: Array[String]): Unit = {

    // 1. 创建sparkSession
    val sparkSession = SparkSession.builder()
      .config("spark.mongodb.output.uri", RECOMMEND_OUTPUT_URI)
      .getOrCreate()
    import sparkSession.implicits._

    // 2. 加载相似度矩阵
    val sim = sparkSession.read
      .format("mongo")
      .option("uri", SIM_MATRIX_OUTPUT_URI)
      .option("collection", SimMatrix(SIM_MEASURE_CHOICE))
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
      .option("uri", DATA_MONGO_URI)
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


    // 5. 为每个用户推荐10个电影
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
        case (userId, iterable) => (userId, iterable.toList.take(10).map(item=>UserRecommendation(item)))
      }
      .toDF("userId", "recommendations")

    // 6. 推荐结果写入MongoDB
    import com.mongodb.spark._
    MongoSpark.save(recommendResultDF)

    sparkSession.stop()
  }
}