package com.yankaizhang.movielikes.recommend.entity

case class Rating(userId: Int,
                  movieId: Int,
                  rating: Double,
                  timestamp: Long)
