package com.yankaizhang.movielikes.recommend.offlineRecommend

import com.yankaizhang.movielikes.recommend.entity.Rating
import com.yankaizhang.movielikes.recommend.util.SimilarityMeasures
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel

/**
 * 基于物品的离线协同过滤
 */
object ItemCFBySparkSQL {

  val MONGO_URL = "mongodb://127.0.0.1:27017/movie_recommend"
  val MONGO_COLLECTION = "ratings"
  val defaultParallelism = 24
  val RECOMMEND_COUNT = 50
  val SIM_MEASURE = "cosSim"
  val SIM_MEASURE_FUNCTION: (Double, Double, Double) => Double = SimilarityMeasures.cosineSimilarity()

  def main(args: Array[String]): Unit = {

    // 1. 创建sparkSession
    val sparkSession = SparkSession.builder()
      .master("local[8]")
      .appName("ItemCFBySparkSQL")
      .config("spark.network.timeout", "10000000")
      .config("spark.executor.memory", "12g")
      .config("spark.executor.cores", "6")
      .config("spark.default.parallelism", defaultParallelism.toString)
      .config("spark.sql.shuffle.partitions", defaultParallelism.toString)
      .config("spark.mongodb.output.uri", "mongodb://127.0.0.1:27017/spark_output.itemCF_recommend_result")
      .getOrCreate()

    // 2. 加载rating数据
    import sparkSession.implicits._
    val ratingDF: DataFrame = sparkSession.read
      .format("mongo")
      .option("uri", MONGO_URL)
      .option("collection", MONGO_COLLECTION)
      .load()
      .as[Rating]
      .map(entity => (entity.userId, entity.movieId, entity.rating))
      .toDF("userId", "movieId", "rating")
      .persist(StorageLevel.MEMORY_AND_DISK_SER)


    // 3. 统计每个电影的评分个数，并通过内连接添加到 ratingDF 中
    val numRatersPerMovie: DataFrame = ratingDF.groupBy("movieId").count().alias("count")
      .coalesce(defaultParallelism)
    val ratingWithCountDF: DataFrame = ratingDF.join(numRatersPerMovie, "movieId")
      .coalesce(defaultParallelism)


    // 4. 对每个userId做 笛卡尔积内联
    ratingWithCountDF.join(ratingWithCountDF, "userId")
      .toDF("userId", "movieId1", "rating1", "count1", "movieId2", "rating2", "count2")
      .selectExpr("userId"
        , "movieId1"
        , "movieId2"
        , "rating1 * rating2 as product"
        , "pow(rating1, 2) as rating1Pow"
        , "pow(rating2, 2) as rating2Pow")
      .coalesce(defaultParallelism)
      .createOrReplaceTempView("userId_joined")


    // 5. 计算物品相似度
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

        val tmp = SIM_MEASURE_FUNCTION(dotProduct, scala.math.sqrt(ratingSumOfSq1), scala.math.sqrt(ratingSumOfSq2))

        (row.getInt(0), row.getInt(1), tmp)
      }).toDF("movieId_01", "movieId_02", SIM_MEASURE).repartition(defaultParallelism)
      .persist(StorageLevel.MEMORY_AND_DISK_SER)


    // 6. 为每个userId生成推荐相似物品的结果
    val userDF: DataFrame = sparkSession.read
      .format("mongo")
      .option("uri", MONGO_URL)
      .option("collection", MONGO_COLLECTION)
      .load()
      .drop("_id", "movieId", "rating", "timestamp")
      .distinct()
      .toDF("userId")
      .persist(StorageLevel.MEMORY_AND_DISK_SER)


    // 7. 选择与用户主动评分的电影
    val userInterestDF: DataFrame = userDF
      .selectExpr("userId as user")
      .join(ratingDF, $"user" <=> ratingDF("userId"), "left")
      .drop($"user")
      .select("userId", "movieId", "rating")
      .persist(StorageLevel.MEMORY_AND_DISK_SER)


    // 8. 根据用户感兴趣的电影，和相似度矩阵中的电影做笛卡尔积，找到和该电影相似的其他电影，建立临时表
    userInterestDF.join(sim, $"movieId" <=> sim("movieId_01"))
      .selectExpr("userId"
        , "movieId_01 as relatedItem"
        , "movieId_02 as otherItem"
        , SIM_MEASURE
        , s"$SIM_MEASURE * rating as simProduct")
      .where("simProduct >= 3.5")
      .coalesce(defaultParallelism)
      .createOrReplaceTempView("tempTable")


    // 9. 为每个用户推荐RECOMMEND_COUNT个电影
    val recommendResultDF: DataFrame = sparkSession.sql(
      s"""
         |SELECT userId
         |,  otherItem
         |,  sum(simProduct) / sum($SIM_MEASURE) as rating
         |FROM tempTable
         |GROUP BY userId, otherItem
         |ORDER BY userId asc, rating desc
      """.stripMargin)
      .rdd
      .mapPartitions(part => {
        part.map(row => (row.getInt(0), (row.getInt(1), row.getDouble(2))))
      })
      .aggregateByKey[Seq[(Int, Double)]](Seq[(Int, Double)]())(_ :+ _, _ ++ _)
      .mapValues(xs => {
        var sequence = Seq[(Int, Double)]()
        val iter = xs.iterator
        var count = 0
        while (iter.hasNext && count < RECOMMEND_COUNT) {
          val rat = iter.next()
          if (rat._2 != Double.NaN)
            sequence :+= (rat._1, rat._2)
          count += 1
        }
        sequence
      })
      .toDF("userId", "recommendations")
      .persist(StorageLevel.MEMORY_AND_DISK_SER)

    // 10. 推荐结果写入MongoDB
    import com.mongodb.spark._
    MongoSpark.save(recommendResultDF)

    sparkSession.stop()
  }
}
