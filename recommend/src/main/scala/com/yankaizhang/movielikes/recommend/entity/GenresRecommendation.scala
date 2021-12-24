package com.yankaizhang.movielikes.recommend.entity


/**
 * 电影类别推荐
 *
 * @param genres 电影类别
 * @param recs   top10的电影集合
 */
case class GenresRecommendation(comedy: String, recs: Seq[Recommendation])
