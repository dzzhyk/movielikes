package com.yankaizhang.movielikes.recommend.staticRecommend

import java.text.SimpleDateFormat
import java.util.Date
import com.yankaizhang.movielikes.recommend.entity.{Movie, Rating}
import com.yankaizhang.movielikes.recommend.util.MongoDBUtil
import org.apache.spark.sql.SparkSession

/**
 * 基于统计的电影推荐
 */
object StaticMovieRecommend {

  val defaultParallelism = 960
  val MOST_SCORE_OF_NUMBER = 10
  val MONGODB_RATING_COLLECTION = "ratings"
  val MONGODB_MOVIE_COLLECTION = "movies"
  val RATE_MORE_MOVIES = "RateMoreMovies"
  val RATE_MORE_RECENTLY_MOVIES = "RateMoreRecentlyMovies"
  val AVERAGE_MOVIES = "AverageMovies"

//  val GENRES_TOP_MOVIES = "GenresTopMovies"
//  val AVERAGE_COMEDY = "AverageComedy"

  def main(args: Array[String]): Unit = {

    // 创建sparkSession
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("StaticMovieRecommend")
      .config("spark.default.parallelism", defaultParallelism.toString)
      .config("spark.sql.shuffle.partitions", defaultParallelism.toString)
      .config("spark.mongodb.input.uri", "mongodb://127.0.0.1:27017/movie_recommend_big")
      .config("spark.mongodb.output.uri", "mongodb://127.0.0.1:27017/spark_output")
      .getOrCreate()
    spark.sparkContext.setLogLevel("WARN")


    // 创建ratings表
    import spark.implicits._
    val ratingDF = MongoDBUtil.readDFInMongoDB(spark, MONGODB_RATING_COLLECTION).drop("_id").as[Rating].toDF()
    val movieDF = MongoDBUtil.readDFInMongoDB(spark, MONGODB_MOVIE_COLLECTION).drop("_id").as[Movie].toDF()
    ratingDF.createOrReplaceTempView("ratings")


    // 1. 统计历史热门电影
    val rateMoreMoviesDF = spark.sql("select movieId,count(movieId) as count from ratings group by movieId")
    MongoDBUtil.storeDFInMongoDB(rateMoreMoviesDF, RATE_MORE_MOVIES)


    // 2. 统计最近热门电影
    val simpleDateFormat = new SimpleDateFormat("yyyyMM")
    spark.udf.register("changeDate", (x: Int) => simpleDateFormat.format(new Date(x * 1000L)).toInt)
    val ratingOfYearMonth = spark.sql("select movieId, rating, changeDate(timestamp) as yearmonth from ratings")
    ratingOfYearMonth.createOrReplaceTempView("ratingOfMonth")
    val rateMoreRecentlyMovies = spark.sql("select movieId, count(movieId) as count, yearmonth from ratingOfMonth group by yearmonth, movieId")
    MongoDBUtil.storeDFInMongoDB(rateMoreRecentlyMovies, RATE_MORE_RECENTLY_MOVIES)


    // 3. 统计电影的平均评分
    val averageMoviesDF = spark.sql("select movieId, avg(rating) as avg from ratings group by movieId")
    MongoDBUtil.storeDFInMongoDB(averageMoviesDF, AVERAGE_MOVIES)

    spark.stop()
  }
}