package com.yankaizhang.movielikes.srv.service.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.yankaizhang.movielikes.srv.constant.MongoConstants;
import com.yankaizhang.movielikes.srv.constant.RedisConstants;
import com.yankaizhang.movielikes.srv.entity.SysMovie;
import com.yankaizhang.movielikes.srv.entity.vo.MovieVO;
import com.yankaizhang.movielikes.srv.mapper.SysMovieMapper;
import com.yankaizhang.movielikes.srv.redis.RedisCache;
import com.yankaizhang.movielikes.srv.service.IRecommendService;
import com.yankaizhang.movielikes.srv.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 推荐服务接口 实现类
 *
 * @author dzzhyk
 */
@Slf4j
@Service
public class RecommendServiceImpl implements IRecommendService {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SysMovieMapper movieMapper;


    @Override
    public List<MovieVO> getMostRated() {
        List<MovieVO> res = redisCache.getCacheList(RedisConstants.MOST_RATED_MOVIES);
        if (StringUtils.isNotNull(res) && StringUtils.isNotEmpty(res)) {
            log.info("getMostRated 缓存命中");
            return res;
        }
        MongoCollection<Document> rateMoreMoviesCollection =
                mongoClient.getDatabase(MongoConstants.MONGODB_DATABASE).getCollection(MongoConstants.MONGODB_RATE_MORE_MOVIES_COLLECTION);
        FindIterable<Document> documents = rateMoreMoviesCollection.find().sort(Sorts.descending("count")).limit(10);

        List<SysMovie> tmp = getMovieListFromDb(documents);
        Map<String, String> avgRatings = getAvgRatings();
        for (SysMovie sysMovie : tmp) {
            res.add(new MovieVO(sysMovie, avgRatings.get(sysMovie.getMovieId().toString())));
        }

        redisCache.setCacheList(RedisConstants.MOST_RATED_MOVIES, res);
        redisCache.expire(RedisConstants.MOST_RATED_MOVIES, 12, TimeUnit.HOURS);
        return res;
    }

    @Override
    public List<MovieVO> getMostRatedRecently() {
        List<MovieVO> res = redisCache.getCacheList(RedisConstants.RECENT_MOST_RATED_MOVIES);
        if (StringUtils.isNotNull(res) && StringUtils.isNotEmpty(res)) {
            log.info("getMostRatedRecently 缓存命中");
            return res;
        }
        MongoCollection<Document> rateMoreMoviesRecentlyCollection = mongoClient.getDatabase(MongoConstants.MONGODB_DATABASE)
                .getCollection(MongoConstants.MONGODB_RATE_MORE_MOVIES_RECENTLY_COLLECTION);
        FindIterable<Document> documents = rateMoreMoviesRecentlyCollection.find().sort(Sorts.descending("yearmonth")).limit(10);

        List<SysMovie> tmp = getMovieListFromDb(documents);
        Map<String, String> avgRatings = getAvgRatings();
        for (SysMovie sysMovie : tmp) {
            res.add(new MovieVO(sysMovie, avgRatings.get(sysMovie.getMovieId().toString())));
        }

        redisCache.setCacheList(RedisConstants.RECENT_MOST_RATED_MOVIES, res);
        redisCache.expire(RedisConstants.RECENT_MOST_RATED_MOVIES, 12, TimeUnit.HOURS);
        return res;
    }

    @Override
    public List<MovieVO> getUserRecommend(Long userId) {
        List<MovieVO> res = redisCache.getCacheList(RedisConstants.USER_RECOMMEND_PREFIX + userId);
        if (StringUtils.isNotNull(res) && StringUtils.isNotEmpty(res)) {
            log.info("getUserRecommend 缓存命中");
            return res;
        }
        Document userDocument = mongoClient.getDatabase(MongoConstants.MONGODB_DATABASE).getCollection(MongoConstants.MONGODB_ITEMCF_RESULT_BIG)
                .find(Filters.eq("userId", userId)).first();
        if (StringUtils.isNull(userDocument) || userDocument.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, String> avgRatings = getAvgRatings();

        List<Long> recommendations = userDocument.get("recommendations", List.class);

        List<SysMovie> tmp = movieMapper.selectBatchIds(recommendations);
        for (SysMovie sysMovie : tmp) {
            res.add(new MovieVO(sysMovie, avgRatings.get(sysMovie.getMovieId().toString())));
        }
        redisCache.setCacheList(RedisConstants.USER_RECOMMEND_PREFIX + userId, res);
        redisCache.expire(RedisConstants.USER_RECOMMEND_PREFIX + userId, 12, TimeUnit.HOURS);
        return res;
    }

    private Map<String, String> getAvgRatings(){
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        Map<String, String> avgRatings = redisCache.getCacheMap(RedisConstants.MOVIE_AVG_RATINGS);
        if (StringUtils.isNull(avgRatings) || StringUtils.isEmpty(avgRatings)) {
            MongoCollection<Document> movieAvgRatings =
                    mongoClient.getDatabase(MongoConstants.MONGODB_DATABASE).getCollection(MongoConstants.MONGODB_AVERAGE_MOVIES_COLLECTION);
            FindIterable<Document> movieDocuments = movieAvgRatings.find();
            avgRatings = new HashMap<>();
            for (Document document : movieDocuments) {
                avgRatings.put(document.getInteger("movieId").toString(), decimalFormat.format(document.getDouble("avg")));
            }
            redisCache.setCacheMap(RedisConstants.MOVIE_AVG_RATINGS, avgRatings);
            redisCache.expire(RedisConstants.MOVIE_AVG_RATINGS, 12, TimeUnit.HOURS);
            log.info("存储avgRating至缓存...");
        } else {
            log.info("命中avgRating缓存");
        }
        return avgRatings;
    }

    private List<SysMovie> getMovieListFromDb(FindIterable<Document> documents) {
        List<SysMovie> res;
        List<Integer> movieIds = new ArrayList<>(10);
        for (Document document : documents) {
            movieIds.add(document.getInteger("movieId"));
        }
        res = movieMapper.selectBatchIds(movieIds);
        return res;
    }

}
