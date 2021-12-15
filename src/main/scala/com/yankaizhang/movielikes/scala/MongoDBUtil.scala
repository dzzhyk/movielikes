package com.yankaizhang.movielikes.scala

import org.apache.spark.sql.{DataFrame, SparkSession}

object MongoDBUtil {

  def storeDFInMongoDB(df: DataFrame, collection_name: String): Unit = {
    import com.mongodb.spark._
    MongoSpark.save(
      df.write
        .option("collection", collection_name)
        .format("com.mongodb.spark.sql")
        .mode("overwrite"))
  }

  def readDFInMongoDB(spark: SparkSession, collection_name: String): DataFrame = {
    spark
      .read
      .option("collection", collection_name)
      .format("com.mongodb.spark.sql")
      .load()
  }


}
