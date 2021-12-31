package com.yankaizhang.movielikes.srv.service.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.yankaizhang.movielikes.srv.constant.MongoConstants;
import com.yankaizhang.movielikes.srv.constant.RedisConstants;
import com.yankaizhang.movielikes.srv.entity.SysMovie;
import com.yankaizhang.movielikes.srv.mapper.SysMovieMapper;
import com.yankaizhang.movielikes.srv.redis.RedisCache;
import com.yankaizhang.movielikes.srv.service.ISysMovieService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yankaizhang.movielikes.srv.util.StringUtils;
import org.bson.BsonDocument;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 电影表 服务实现类
 * </p>
 *
 * @author dzzhyk
 * @since 2021-12-28
 */
@Service
public class SysMovieServiceImpl extends ServiceImpl<SysMovieMapper, SysMovie> implements ISysMovieService {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private SysMovieMapper movieMapper;

    @Autowired
    private RedisCache redisCache;

    @Override
    public Map<String, Object> getMovieDetails(Long movieId) {
        Map<String, Object> res = redisCache.getCacheMap(RedisConstants.MOVIE_DETAIL_PREFIX + movieId);
        if (StringUtils.isNotNull(res) && StringUtils.isNotEmpty(res)){
            return res;
        }
        res = new HashMap<>();
        SysMovie sysMovie = movieMapper.selectById(movieId);
        if (StringUtils.isNull(sysMovie)) {
            return res;
        }
        res.put("movie", sysMovie);

        // 获取10个相似电影
        MongoCollection<Document> simMatrix
                = mongoClient.getDatabase(MongoConstants.MONGODB_DATABASE).getCollection(MongoConstants.MONGODB_ITEMCF_SIM_MATRIX_COLLECTION);
        FindIterable<Document> documents = simMatrix.find(Filters.eq("movieId1", movieId)).sort(Sorts.descending("similarity")).limit(10);
        List<Integer> simMovieIds = new ArrayList<>(8);
        for (Document document : documents) {
            simMovieIds.add(document.getInteger("movieId2"));
        }
        List<SysMovie> simMovies = movieMapper.selectBatchIds(simMovieIds);
        res.put("simMovies", simMovies);
        redisCache.setCacheMap(RedisConstants.MOVIE_DETAIL_PREFIX + movieId, res);
        redisCache.expire(RedisConstants.MOVIE_DETAIL_PREFIX + movieId, 12, TimeUnit.HOURS);
        return res;
    }
}
