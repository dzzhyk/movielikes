package com.yankaizhang.movielikes.recommend.offlineRecommend

import com.yankaizhang.movielikes.recommend.constant.SimilarityMeasureConstant
import com.yankaizhang.movielikes.recommend.entity.Rating
import com.yankaizhang.movielikes.recommend.constant.OfflineConstant
import com.yankaizhang.movielikes.recommend.util.SimilarityMeasures
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel

/**
 * 改进余弦相似度
 * 避免头部物品效应，保证推荐几率公平
 */
object ImpCosSim {

  def main(args: Array[String]): Unit = {

    // 1. 创建sparkSession
    val uri = OfflineConstant.MONGO_DB_HOST + OfflineConstant.SPARK_MONGO_OUTPUT + "." + OfflineConstant.SIM_MEASURE_MAP(SimilarityMeasureConstant.IMP_COS_SIM)
    val sparkSession = SparkSession.builder()
      .config("spark.mongodb.output.uri", uri)
      .getOrCreate()
    sparkSession.sparkContext.setLogLevel("WARN")
    import sparkSession.implicits._

    // 2. 加载rating数据
    val ratingDF: DataFrame = sparkSession.read
      .format("mongo")
      .option("uri", OfflineConstant.MONGO_DB_HOST + OfflineConstant.MOVIELENS_COLLECTION_NAME)
      .option("collection", "ratings")
      .load()
      .drop("_id")
      .as[Rating]
      .map(entity => (entity.userId, entity.movieId, entity.rating))
      .toDF("userId", "movieId", "rating")
      .persist(StorageLevel.MEMORY_ONLY_SER)


    // 3. 统计每个电影的评分个数，并通过内连接添加到 ratingDF 中
    val numRatersPerMovie: DataFrame = ratingDF.groupBy("movieId").count().alias("count")
    val ratingWithCountDF: DataFrame = ratingDF.join(numRatersPerMovie, "movieId")


    // 4. 对每个userId做 笛卡尔积内联
    ratingWithCountDF.join(ratingWithCountDF, "userId")
      .toDF("userId", "movieId1", "rating1", "count1", "movieId2", "rating2", "count2")
      .selectExpr("userId"
        , "movieId1", "movieId2"
        , "count1", "count2"
        , "rating1 * rating2 as product", "rating2"
        , "pow(rating1, 2) as rating1Pow")
      .createOrReplaceTempView("userId_joined")


    // 5. 计算TopK物品相似度
    val sim = sparkSession.sql(
      """
        |SELECT movieId1
        |, movieId2
        |, sum(product) as dotProduct
        |, sum(rating1Pow) as  ratingSumOfSq1
        |, sum(rating2) as ratingSum2
        |, count(userId) as size
        |, first(count1) as count1
        |, first(count2) as count2
        |FROM userId_joined
        |GROUP BY movieId1, movieId2
      """.stripMargin)
      .coalesce(OfflineConstant.DEFAULT_PARALLELISM)
      .map(row => {
        val dotProduct = row.getAs[Double](2)
        val ratingSumOfSq1 = row.getAs[Double](3)
        val ratingSum2 = row.getAs[Double](4)
        val size = row.getAs[Long](5)
        val count1 = row.getAs[Long](6)
        val count2 = row.getAs[Long](7)

        val impCosSim = SimilarityMeasures.improvedCosineSimilarity(dotProduct, math.sqrt(ratingSumOfSq1), math.sqrt(ratingSum2), size, count1, count2)

        (row.getInt(0), (row.getInt(1), impCosSim))
      })
      .rdd
      .groupByKey()
      .mapPartitions(part => {
        part.map {
          case (movieId, recs) =>
            (movieId, recs.toList.filter(x => x._1 != movieId).sortWith(_._2 > _._2).map(x => (x._1, x._2)).take(OfflineConstant.SIM_MOVIE_PER))
        }
      })
      .flatMapValues(value => value)
      .map(tup => (tup._1, tup._2._1, tup._2._2))
      .toDF("movieId1", "movieId2", "similarity")

    // 6. 保存相似度矩阵
    import com.mongodb.spark._
    MongoSpark.save(sim.write.mode("overwrite"))

    sparkSession.stop()
  }

}