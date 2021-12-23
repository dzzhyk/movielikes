package com.yankaizhang.movielikes.recommend.offlineRecommend

import com.yankaizhang.movielikes.recommend.entity.Rating
import com.yankaizhang.movielikes.recommend.util.SimilarityMeasures
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel

/**
 * 基于物品的离线协同过滤
 * 使用余弦相似度
 */
object ItemCFByCosSim {

  val MONGO_URL = "mongodb://127.0.0.1:27017/movie_recommend"
  val MONGO_COLLECTION = "ratings"
  val defaultParallelism = 72
  val RECOMMEND_COUNT = 10

  def main(args: Array[String]): Unit = {

    // 1. 创建sparkSession
    val sparkSession = SparkSession.builder()
      .getOrCreate()

    import sparkSession.implicits._
    val collectionDF = sparkSession.read
      .format("mongo")
      .option("uri", MONGO_URL)
      .option("collection", MONGO_COLLECTION)
      .load()


    // 2. 加载rating数据
    val ratingDF: DataFrame = collectionDF
      .as[Rating]
      .map(entity => (entity.userId, entity.movieId, entity.rating))
      .toDF("userId", "movieId", "rating")
      .persist(StorageLevel.MEMORY_ONLY_SER)


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
          // 召回阶段，过滤出Top20
          case (movieId, recs) =>
            (movieId, recs.toList.filter(x => x._1 != movieId).sortWith(_._2 > _._2).map(x => (x._1, x._2)).take(RECOMMEND_COUNT*2))
        }
      })
      .flatMapValues(value => value)
      .mapPartitions(part => {
        part.map(tup => (tup._1, tup._2._1, tup._2._2))
      })
      .toDF("movieId_01", "movieId_02", "cosSim")
      .repartition(defaultParallelism)
      .persist(StorageLevel.MEMORY_ONLY_SER)


    // 6. 获取所有userId
    val userDF: DataFrame = collectionDF
      .drop("_id", "movieId", "rating", "timestamp")
      .distinct()
      .toDF("userId")
      .persist(StorageLevel.MEMORY_ONLY_SER)


    // 7. 选择与用户主动评分的电影
    val userInterestDF: DataFrame = userDF
      .selectExpr("userId as user")
      .join(ratingDF, $"user" <=> ratingDF("userId"), "left")
      .drop($"user")
      .select("userId", "movieId", "rating")
      .persist(StorageLevel.MEMORY_ONLY_SER)


    // 8. 根据用户感兴趣的电影，和TopK召回相似物品矩阵中的电影做笛卡尔积，找到和该电影相似的其他电影，建立临时表
    userInterestDF.join(sim, $"movieId" <=> sim("movieId_01"))
      .selectExpr("userId"
        , "movieId_01 as relatedItem"
        , "movieId_02 as otherItem"
        , "cosSim"
        , "cosSim * rating as simProduct")
      .coalesce(defaultParallelism)
      .createOrReplaceTempView("tempTable")


    // 9. 为每个用户推荐10个电影
    val recommendResultDF: DataFrame = sparkSession.sql(
      """|SELECT userId
         |,  otherItem
         |,  sum(simProduct) / sum(cosSim) as rating
         |FROM tempTable
         |GROUP BY userId, otherItem
      """.stripMargin)
      .rdd
      .mapPartitions(part => {
        part.map(row => (row.getInt(0), (row.getInt(1), row.getDouble(2))))
      })
      .groupByKey()
      .mapPartitions(part => {
        part.map {
          case (userId, iterable) =>
            (userId, iterable.toList.sortWith(_._2 > _._2).take(RECOMMEND_COUNT))
        }
      })
      .toDF("userId", "recommendations")

    // 10. 推荐结果写入MongoDB
    import com.mongodb.spark._
    MongoSpark.save(recommendResultDF)

    sparkSession.stop()
  }
}
