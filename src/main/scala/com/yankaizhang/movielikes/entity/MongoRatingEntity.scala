package com.yankaizhang.movielikes.entity

case class MongoRatingEntity(userId: Int,
                             movieId: Int,
                             rating: Double,
                             timestamp: Long)
