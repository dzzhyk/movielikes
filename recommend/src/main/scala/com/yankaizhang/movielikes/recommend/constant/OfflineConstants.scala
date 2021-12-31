package com.yankaizhang.movielikes.recommend.constant

import com.yankaizhang.movielikes.recommend.constant.SimilarityMeasureConstants.{COS_SIM, CO_OCCUR_SIM, IMP_COS_SIM}

/**
 * 离线推荐参数配置
 */
object OfflineConstants {

  // 是否为大数据
  val BIG_DATA: Boolean = true
  // 预测相似度
  val SIM_MEASURE_CHOICE: String = IMP_COS_SIM
  // MongoDB连接
  val MONGO_DB_HOST: String = "mongodb://127.0.0.1:27017/"
  // 使用的ratings数据collectionName
  val MOVIELENS_COLLECTION_NAME: String = (if (BIG_DATA) "movie_recommend_big" else "movie_recommend")
  // spark输出目录dbName
  val SPARK_MONGO_OUTPUT: String = "spark_output"
  // 相似度计算结果collectionName
  val SIM_MEASURE_MAP = Map(
    COS_SIM -> (if (BIG_DATA) "itemCF_cosSim_simMatrix_big" else "itemCF_cosSim_simMatrix"),
    IMP_COS_SIM -> (if (BIG_DATA) "itemCF_impCosSim_simMatrix_big" else "itemCF_impCosSim_simMatrix"),
    CO_OCCUR_SIM -> (if (BIG_DATA) "itemCF_coOccurSim_simMatrix_big" else "itemCF_coOccurSim_simMatrix")
  )
  // 离线推荐结果collectionName
  val OFFLINE_RECOMMEND_OUTPUT: String = (if (BIG_DATA) "itemCF_result_big" else "itemCF_result")
  // 并行度
  val DEFAULT_PARALLELISM = 960
  // 为每个电影求的相似电影数量
  val SIM_MOVIE_PER = 10
  // 为每个用户推荐的电影数量
  val RECOMMEND_COUNT_PER = 10

}