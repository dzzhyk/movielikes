package com.yankaizhang.movielikes.recommend.offlineRecommend

import com.yankaizhang.movielikes.recommend.constant.SimilarityMeasureConstant
import com.yankaizhang.movielikes.recommend.entity.Rating
import com.yankaizhang.movielikes.recommend.util.SimilarityMeasures
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel
import com.yankaizhang.movielikes.recommend.offlineRecommend.OfflineMovieRecommend.{DATA_MONGO_URI, SIM_MATRIX_OUTPUT_URI, SimMatrix, defaultParallelism}

/**
 * 余弦相似度
 */
object CosSim {

  def main(args: Array[String]): Unit = {

    // 1. 创建sparkSession
    val sparkSession = SparkSession.builder()
      .config("spark.mongodb.output.uri", SIM_MATRIX_OUTPUT_URI + "." + SimMatrix(SimilarityMeasureConstant.COS_SIM))
      .getOrCreate()

    import sparkSession.implicits._

    // 2. 加载rating数据
    val ratingDF: DataFrame = sparkSession.read
      .format("mongo")
      .option("uri", DATA_MONGO_URI)
      .option("collection", "ratings")
      .load()
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
        , "movieId1"
        , "movieId2"
        , "rating1 * rating2 as product"
        , "pow(rating1, 2) as rating1Pow"
        , "pow(rating2, 2) as rating2Pow")
      .createOrReplaceTempView("userId_joined")


    // 5. 计算TopK物品相似度
    val sim = sparkSession.sql(
      """
        |SELECT movieId1
        |, movieId2
        |, sum(product) as dotProduct
        |, sum(rating1Pow) as  ratingSumOfSq1
        |, sum(rating2Pow)  as  ratingSumOfSq2
        |FROM userId_joined
        |GROUP BY movieId1, movieId2
      """.stripMargin)
      .coalesce(defaultParallelism)
      .map(row => {
        val dotProduct = row.getAs[Double](2)
        val ratingSumOfSq1 = row.getAs[Double](3)
        val ratingSumOfSq2 = row.getAs[Double](4)

        val cosSim = SimilarityMeasures.cosineSimilarity(dotProduct, scala.math.sqrt(ratingSumOfSq1), scala.math.sqrt(ratingSumOfSq2))

        (row.getInt(0), (row.getInt(1), cosSim))
      })
      .rdd
      .groupByKey()
      .mapPartitions(part => {
        part.map {
          case (movieId, recs) =>
            (movieId, recs.toList.filter(x => x._1 != movieId).sortWith(_._2 > _._2).map(x => (x._1, x._2)).take(10))
        }
      })
      .flatMapValues(value => value)
      .map(tup => (tup._1, tup._2._1, tup._2._2))
      .toDF("movieId1", "movieId2", "similarity")

    // 6. 保存相似度矩阵
    import com.mongodb.spark._
    MongoSpark.save(sim)

    sparkSession.stop()
  }

}