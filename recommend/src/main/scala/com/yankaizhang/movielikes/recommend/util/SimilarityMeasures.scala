package com.yankaizhang.movielikes.recommend.util


/**
 * 相似度公式大全
 */
object SimilarityMeasures {
  /**
   * 共现相似度
   * The Co-occurrence similarity between two vectors A, B is
   * |N(i) ∩ N(j)| / sqrt(|N(i)||N(j)|)
   */
  def coOccurrence(numOfRatersForAAndB: Long, numOfRatersForA: Long, numOfRatersForB: Long): Double = {
    numOfRatersForAAndB / math.sqrt(numOfRatersForA * numOfRatersForB)
  }

  /**
   * 皮尔逊积矩相关系数 两个变量之间的协方差和标准差的商
   * The correlation between two vectors A, B is
   * cov(A, B) / (stdDev(A) * stdDev(B))
   *
   * This is equivalent to
   * [n * dotProduct(A, B) - sum(A) * sum(B)] /
   * sqrt{ [n * norm(A)^2 - sum(A)^2] [n * norm(B)^2 - sum(B)^2] }
   */
  def correlation(size: Double, dotProduct: Double, ratingSum: Double,
                  rating2Sum: Double, ratingNormSq: Double, rating2NormSq: Double): Double = {

    val numerator = size * dotProduct - ratingSum * rating2Sum
    val denominator = scala.math.sqrt(size * ratingNormSq - ratingSum * ratingSum) *
      scala.math.sqrt(size * rating2NormSq - rating2Sum * rating2Sum)

    numerator / denominator
  }

  /**
   * 余弦相似度
   * The cosine similarity between two vectors A, B is
   * dotProduct(A, B) / (norm(A) * norm(B))
   */
  def cosineSimilarity(dotProduct: Double, ratingNorm: Double, rating2Norm: Double): Double = {
    dotProduct / (ratingNorm * rating2Norm)
  }

  /**
   * 改进余弦相似度
   * The improved cosine similarity between two vectors A, B is
   * dotProduct(A, B) * num(A ∩ B) / (norm(A) * norm(B) * num(A) * log10(10 + num(B)))
   */
  def improvedCosineSimilarity(dotProduct: Double, ratingNorm: Double, rating2Norm: Double, numAjoinB: Long, numA: Long, numB: Long): Double = {
    dotProduct * numAjoinB / (ratingNorm * rating2Norm * numA * math.log10(10 + numB))
  }

  /**
   * jaccard相似度
   * The Jaccard Similarity between two sets A, B is
   * |Intersection(A, B)| / |Union(A, B)|
   */
  def jaccardSimilarity(usersInCommon: Double, totalUsers1: Double, totalUsers2: Double): Double = {
    val union = totalUsers1 + totalUsers2 - usersInCommon
    usersInCommon / union
  }
}