package com.yankaizhang.movielikes.recommend.entity


/**
 * 推荐电影
 *
 * @param movieId 电影推荐的id
 * @param score   电影推荐的评分
 */
case class Recommendation(movieId: Int, score: Double)