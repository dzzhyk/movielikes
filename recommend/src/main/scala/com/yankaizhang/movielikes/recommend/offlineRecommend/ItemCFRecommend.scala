package com.yankaizhang.movielikes.recommend.offlineRecommend

import com.yankaizhang.movielikes.recommend.entity.Rating
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel

import scala.collection.mutable.ArrayBuffer


/**
 * 基于物品的离线协同过滤
 * RDD实现
 */
object ItemCFRecommend {

  val MONGO_URL = "mongodb://127.0.0.1:27017/movie-recommend"
  val MONGO_COLLECTION = "ratings"

  def main(args: Array[String]): Unit = {

    // 初始化spark环境
    val sparkSession = SparkSession.builder()
      .master("local[*]")
      .appName("ItemCFRecommend")
      .config("spark.network.timeout", "10000000")
//      .config("spark.mongodb.output.uri", "mongodb://10.237.53.6:27017/spark-output")
      .config("spark.executor.memory", "12g")
      .config("spark.executor.cores", "4")
      .getOrCreate()

    // 加载最新rating数据
    import sparkSession.implicits._

    val userItemArray: RDD[(Int, Seq[(Int, Double)])] = sparkSession.read
      .format("mongo")
      .option("uri", MONGO_URL)
      .option("collection", MONGO_COLLECTION)
      .load()
      .as[Rating]
      .rdd
      .mapPartitions(part => {
        part.map(rating => (rating.userId, rating.movieId, rating.rating))
      })
      .groupBy(doc => doc._1)
      .mapPartitions(part => {
        part.map(item => (item._1, item._2.map(tup => (tup._2, tup._3)).toSeq))
      })

    var S0 = sparkSession.sparkContext.makeRDD(Seq[((Int, Int), Double)]())

    val tmpBuffer = ArrayBuffer[RDD[((Int, Int), Double)]]()
    tmpBuffer.append(S0)

    val array = userItemArray.collect()

    var cnt = 0
    for (elem <- array) {
      val itemSeq: Seq[(Int, Double)] = elem._2
      val value: RDD[(Int, Double)] = sparkSession.sparkContext.parallelize(itemSeq)
      val tmp: RDD[((Int, Int), Double)] = value.cartesian(value).map(result => {
        ((result._1._1, result._2._1), result._1._2 * result._2._2)
      })
      tmpBuffer.append(tmp)
      cnt += 1
      if (cnt % 20 == 0) {
        S0 = sparkSession.sparkContext.union(tmpBuffer)
        tmpBuffer.clear()
        tmpBuffer.append(S0)
      }
    }
    S0 = sparkSession.sparkContext.union(tmpBuffer)
    tmpBuffer.clear()

    // 求笛卡尔积后groupBy movie元组 求和
    val S1 = S0.reduceByKey((value, key) => value + key).persist(StorageLevel.MEMORY_AND_DISK_SER)

    println("S1.count: ", S1.count())
    println(S1.first().toString())

    val S2 = S1.filter(r => {
      val key = r._1
      if (key._1 == key._2) true
      else false
    }).map({ r => (r._1._1, r._2) }).collectAsMap()

    val _S2 = sparkSession.sparkContext.broadcast(S2)

    // 相似度矩阵
    val simMatrixRDD: DataFrame = S1.filter {
      case (a, _) => a._1 != a._2
    }.mapPartitions(part => {
      part.map(r => {
        val k = r._2 / Math.sqrt(_S2.value(r._1._1) * _S2.value(r._1._2))
        ((r._1._1, r._1._2), k)
      })
    }).toDF()

    _S2.destroy()
    S1.unpersist(true)

    println("相似度矩阵记录数: ", simMatrixRDD.count())
  }

}
