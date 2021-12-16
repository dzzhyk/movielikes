package com.yankaizhang.movielikes.recommend.staticRecommend

import com.yankaizhang.movielikes.recommend.entity.{Movie, Rating}
import com.yankaizhang.movielikes.recommend.util.MongoDBUtil
import java.text.SimpleDateFormat
import java.util.Date

import com.yankaizhang.movielikes.recommend.entity.{GenresRecommendation, Recommendation}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * 基于统计的电影推荐
 */
object StaticMovieRecommend {

  // 设置topk中的k值
  val MOST_SCORE_OF_NUMBER = 10
  // MongoDB中的表名
  val MONGODB_RATING_COLLECTION = "ratings"
  val MONGODB_MOVIE_COLLECTION = "Movie"

  // 统计表的名称
  val RATE_MORE_MOVIES = "RateMoreMovies"
  val RATE_MORE_RECENTLY_MOVIES = "RateMoreRecentlyMovies"
  val AVERAGE_MOVIES = "AverageMovies"
  val GENRES_TOP_MOVIES = "GenresTopMovies"

  def main(args: Array[String]): Unit = {
    /**
     * 设置配置信息
     */
    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://127.0.0.1:27017/movie-recommend",
      "mongo.db" -> "movie-recommend"
    )

    /**
     * 创建spark配置
     */
    // 创建sparkConf配置
    val sparkConf = new SparkConf()
      .setMaster(config("spark.cores"))
      .setAppName("StatisticsRecommender")

    // 创建sparkSession
    val spark = SparkSession.builder().config(sparkConf).config("spark.mongodb.input.uri", config("mongo.uri"))
      .config("spark.mongodb.output.uri", config("mongo.uri"))
      .getOrCreate()


    import spark.implicits._
    val ratingDF = MongoDBUtil.readDFInMongoDB(spark,MONGODB_RATING_COLLECTION).as[Rating].toDF()
    val movieDF = MongoDBUtil.readDFInMongoDB(spark,MONGODB_MOVIE_COLLECTION).as[Movie].toDF()

    // 创建ratings表
    ratingDF.createOrReplaceTempView("ratings")

    /**
     * 统计推荐算法
     */

    /**
     * 统计历史热门电影
     */
    val rateMoreMoviesDF = spark.sql("select movieId,count(movieId) as count from ratings group by movieId")

    MongoDBUtil.storeDFInMongoDB(rateMoreMoviesDF,RATE_MORE_MOVIES)

    /**
     * 统计最近热门电影
     */
    val simpleDateFormat = new SimpleDateFormat("yyyyMM")
    // 用于将rating数据中的timestamp转换成年月格式
    spark.udf.register("changeDate", (x: Int) => simpleDateFormat.format(new Date(x * 1000L)).toInt)

    val ratingOfYearMonth = spark.sql("select movieId, rating, changeDate(timestamp) as yearmonth from ratings")

    // 创建ratingOfMonth表
    ratingOfYearMonth.createOrReplaceTempView("ratingOfMonth")

    // 先以yearmonth分组再以mid分组
    val rateMoreRecentlyMovies = spark.sql("select movieId, count(movieId) as count, yearmonth from ratingOfMonth group by yearmonth, movieId")

    MongoDBUtil.storeDFInMongoDB(rateMoreRecentlyMovies,RATE_MORE_RECENTLY_MOVIES)

    /**
     * 统计电影的平均评分
     */
    val averageMoviesDF = spark.sql("select movieId, avg(rating) as avg from ratings group by movieId")

    MongoDBUtil.storeDFInMongoDB(averageMoviesDF,AVERAGE_MOVIES)


    /**
     * 统计每种类别top10
     */

    // join数据集averageMoviesDF和movieWithScore
    val movieWithScore = movieDF.join(averageMoviesDF, Seq("movieId"))

    // 电影种类
    val genres =
      List("Action", "Adventure", "Animation", "Comedy", "Crime", "Documentary", "Drama", "Family", "Fantasy", "Foreign",
        "History", "Horror", "Music", "Mystery"
        , "Romance", "Science", "Tv", "Thriller", "War", "Western")

    // genres转换为RDD
    val genresRDD = spark.sparkContext.makeRDD(genres)

    // movieWithScore与genres进行笛卡尔积
    val genresTopMovies = genresRDD.cartesian(movieWithScore.rdd)
      .filter {
        // 过滤电影类别不匹配的电影
        case (genres, row) =>
          row.getAs[String]("genres").toLowerCase.contains(genres.toLowerCase)
      }
      .map {
        // 减少数据集的数据量
        case (genres, row) =>
          (genres, ((row.getAs[Int]("movieId")), row.getAs[Double]("avg")))
      }.groupByKey() // 将数据集中的类别相同的电影进行聚合
      .map {
        // 通过评分大小进行降序排序
        case (genres, items) =>
          GenresRecommendation(genres, items.toList.sortWith(_._2 > _._2)
            .take(MOST_SCORE_OF_NUMBER).map(item => Recommendation(item._1, item._2)))
      }.toDF()

    MongoDBUtil.storeDFInMongoDB(genresTopMovies,GENRES_TOP_MOVIES)
    spark.stop()
  }

}