package com.yankaizhang.movielikes.recommend.offlineRecommend

import com.yankaizhang.movielikes.recommend.entity.Rating
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.spark.storage.StorageLevel

/**
 * 基于物品的离线协同过滤
 * SparkSQL实现
 */
object ItemCFBySparkSQL {

  val MONGO_URL = "mongodb://127.0.0.1:27017/movie-recommend"
  val MONGO_COLLECTION = "ratings"
  val defaultParallelism = 96

  def main(args: Array[String]): Unit = {

    val sparkSession = SparkSession.builder()
      .master("local[8]")
      .appName("ItemCFBySparkSQL")
      .config("spark.network.timeout", "10000000")
      .config("spark.executor.memory", "12g")
      .config("spark.executor.cores", "6")
      .config("spark.default.parallelism", defaultParallelism.toString)
      .config("spark.sql.shuffle.partitions", defaultParallelism.toString)
      .config("spark.mongodb.output.uri", "mongodb://127.0.0.1:27017/spark-output.itemCFSQL-simMatrix")
      .getOrCreate()

    import sparkSession.implicits._
    val ratingDF: DataFrame = sparkSession.read
      .format("mongo")
      .option("uri", MONGO_URL)
      .option("collection", MONGO_COLLECTION)
      .load()
      .as[Rating]
      .map(entity => (entity.userId, entity.movieId, entity.rating))
      .toDF("userId", "movieId", "rating")


    // 统计每个电影的评分个数，并通过内连接添加到 ratingDF 中
    val numRatersPerMovie: DataFrame = ratingDF.groupBy("movieId").count().alias("count")
      .coalesce(defaultParallelism)
    val ratingWithCountDF: DataFrame = ratingDF.join(numRatersPerMovie, "movieId")
      .coalesce(defaultParallelism)


    // 对每个userId做 笛卡尔积内联
    ratingWithCountDF.join(ratingWithCountDF, "userId")
      .toDF("userId", "movieId1", "rating1", "count1", "movieId2", "rating2", "count2")
      .selectExpr("userId"
        , "movieId1", "rating1", "count1"
        , "movieId2", "rating2", "count2"
        , "rating1 * rating2 as product"
        , "pow(rating1, 2) as rating1Pow"
        , "pow(rating2, 2) as rating2Pow"
        , "abs(rating1-rating2) as difference"
      )
      .coalesce(defaultParallelism)
      .createOrReplaceTempView("joined")

    // 计算求解不同相似度必须的一些数据
    val sparseMatrix = sparkSession.sql(
      """
        |SELECT movieId1
        |, movieId2
        |, count(userId) as size
        |, sum(product) as dotProduct
        |, sum(rating1) as ratingSum1
        |, sum(rating2) as ratingSum2
        |, sum(rating1Pow) as  ratingSumOfSq1
        |, sum(rating2Pow)  as  ratingSumOfSq2
        |, first(count1) as count1
        |, first(count2) as count2
        |, sum(difference)  as  differenceSum
        |FROM joined
        |GROUP BY movieId1, movieId2
      """.stripMargin)
      .coalesce(defaultParallelism)
      .persist(StorageLevel.MEMORY_AND_DISK_SER)

    //  计算物品相似度
    import com.yankaizhang.movielikes.recommend.util.SimilarityMeasures._
    val sim = sparseMatrix.map(row => {
      val size = row.getAs[Long](2)
      val dotProduct = row.getAs[Double](3)
      val ratingSum1 = row.getAs[Double](4)
      val ratingSum2 = row.getAs[Double](5)
      val ratingSumOfSq1 = row.getAs[Double](6)
      val ratingSumOfSq2 = row.getAs[Double](7)
      val numRaters1 = row.getAs[Long](8)
      val numRaters2 = row.getAs[Long](9)
      var differenceSum = row.getAs[Double](10)
      if (size == 0) {
        differenceSum = 1000000000.0
      } else {
        differenceSum /= size
      }

      val coOc = coOccurrence(size, numRaters1, numRaters2)
      val corr = correlation(size, dotProduct, ratingSum1, ratingSum2, ratingSumOfSq1, ratingSumOfSq2)
      val cosSim = cosineSimilarity(dotProduct, scala.math.sqrt(ratingSumOfSq1), scala.math.sqrt(ratingSumOfSq2))
      val impCosSim = improvedCosineSimilarity(dotProduct, math.sqrt(ratingSumOfSq1), math.sqrt(ratingSum2), size, numRaters1, numRaters2)
      val jaccard = jaccardSimilarity(size, numRaters1, numRaters2)


      (row.getInt(0), row.getInt(1), coOc, corr, cosSim, impCosSim, jaccard, differenceSum)
    }).toDF("movieId_01", "movieId_02", "coOc", "corr", "cosSim", "impCosSim", "jaccard", "differenceSum")

    val simMatrixDS: Dataset[Row] = sim
      .repartition(defaultParallelism) // 重新分区，以便数据均匀分布，方便继续使用
      .persist(StorageLevel.MEMORY_AND_DISK_SER)

    import com.mongodb.spark._
    MongoSpark.save(simMatrixDS)
  }
}
