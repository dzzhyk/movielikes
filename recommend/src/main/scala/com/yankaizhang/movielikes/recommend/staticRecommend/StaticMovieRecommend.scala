package com.yankaizhang.movielikes.recommend.staticRecommend

import java.text.SimpleDateFormat
import java.util.Date
import com.yankaizhang.movielikes.recommend.entity.{UserComedyTable, GenresRecommendation, Movie, Rating, Recommendation}

import com.yankaizhang.movielikes.recommend.util.MongoDBUtil
import org.apache.spark.sql.SparkSession

/**
 * 基于统计的电影推荐
 */
object StaticMovieRecommend {

  val defaultParallelism = 20

  // 设置topK
  val MOST_SCORE_OF_NUMBER = 10

  // MongoDB中的表名
  val MONGODB_RATING_COLLECTION = "ratings"
  val MONGODB_MOVIE_COLLECTION = "movies"

  // 统计表collection的名称
  val RATE_MORE_MOVIES = "RateMoreMovies"
  val RATE_MORE_RECENTLY_MOVIES = "RateMoreRecentlyMovies"
  val AVERAGE_MOVIES = "AverageMovies"
  val GENRES_TOP_MOVIES = "GenresTopMovies"
  val AVERAGE_COMEDY = "AverageComedy"

  def main(args: Array[String]): Unit = {

    // 创建sparkSession
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("StaticMovieRecommend")
      .config("spark.default.parallelism", defaultParallelism.toString)
      .config("spark.sql.shuffle.partitions", defaultParallelism.toString)
      .config("spark.mongodb.input.uri", "mongodb://127.0.0.1:27017/movie_recommend")
      .config("spark.mongodb.output.uri", "mongodb://127.0.0.1:27017/spark_output")
      .getOrCreate()

    // 创建ratings表
    import spark.implicits._
    val ratingDF = MongoDBUtil.readDFInMongoDB(spark, MONGODB_RATING_COLLECTION).as[Rating].toDF()
    val movieDF = MongoDBUtil.readDFInMongoDB(spark, MONGODB_MOVIE_COLLECTION).as[Movie].toDF()
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

    // 4. 统计每种类别top10
    val movieWithScore = movieDF.join(averageMoviesDF, Seq("movieId"))

    val comedy = List("Action", "Adventure", "Animation", "Children","Comedy", "Crime", "Documentary", "Drama", "Fantasy", "Film-Noir", "Horror", "IMAX", "Musical","Mystery", "Romance", "Sci-Fi", "Thriller", "War", "Western")
    val genresRDD = spark.sparkContext.makeRDD(comedy)

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

    MongoDBUtil.storeDFInMongoDB(genresTopMovies, GENRES_TOP_MOVIES)

    //rating join movie->  aa: userId,movieId,rating,timestamp,title,genres
    //comedy 笛卡尔积aa + filter+ map-> bb: comedy,userId,movieId,rating,yearmonth,title,genres
    //cc: select userId,comedy,avg(Degree(yearmonth,rating)) as comedyScore from bb group by userId,comedy
    //
    spark.udf.register("Degree",(yearMonth:Int,rating:Double)=>{
      val now=new Date()
      val timeDifference=simpleDateFormat.format(now).toInt-yearMonth
      (math.E-math.log((timeDifference/20)+1)+1)/2+rating
    })
    //rating join movie -> aa: userId,movieId,rating,timestamp,title,genres
    val aaDF=ratingDF.join(movieDF,Seq("movieId"))
    val bbDF = genresRDD.cartesian(aaDF.rdd).filter{
      case (comedy, row) => {
        row.getAs[String]("genres").toLowerCase.contains(comedy.toLowerCase)
      }
    }.map{
      case (comedy, row) => {
        val x=row.getAs[Int]("timestamp")
        val yearmonth=simpleDateFormat.format(new Date(x * 1000L)).toInt
        UserComedyTable(row.getAs[Int]("userId"),comedy,row.getAs[Double]("rating"),yearmonth)
      }
    }.toDF()
    bbDF.createOrReplaceTempView("bb")
    val ccDF=spark.sql("select userId,comedy,avg(Degree(yearmonth,rating)) as comedyScore from bb group by userId,comedy")
    MongoDBUtil.storeDFInMongoDB(ccDF, AVERAGE_COMEDY)
    spark.stop()
  }

  //userId,genres

}