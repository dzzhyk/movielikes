package com.yankaizhang.movielikes.recommend.entity

case class MongoRatingEntity(userId: Int,
                             movieId: Int,
                             rating: Double,
                             timestamp: Long)
