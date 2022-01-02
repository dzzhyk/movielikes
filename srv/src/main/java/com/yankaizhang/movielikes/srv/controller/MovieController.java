package com.yankaizhang.movielikes.srv.controller;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import com.yankaizhang.movielikes.srv.api.AjaxResult;
import com.yankaizhang.movielikes.srv.constant.MongoConstants;
import com.yankaizhang.movielikes.srv.entity.SysUser;
import com.yankaizhang.movielikes.srv.service.ISysMovieService;
import com.yankaizhang.movielikes.srv.util.SecurityUtils;
import com.yankaizhang.movielikes.srv.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 电影接口
 *
 * @author dzzhyk
 */
@Api("电影接口")
@Slf4j
@RestController
@RequestMapping("/movie")
public class MovieController {

    @Autowired
    private ISysMovieService sysMovieService;

    @Autowired
    private MongoClient mongoClient;

    @ApiOperation("获取电影详情")
    @GetMapping("/detail/{mid}")
    public AjaxResult getMovieDetails(@PathVariable("mid") Long movieId) {
        Map<String, Object> result = sysMovieService.getMovieDetails(movieId);
        return AjaxResult.success(result);
    }

    @ApiOperation("分页获取电影")
    @GetMapping("/list")
    public AjaxResult getMovieListPage(@RequestParam(value = "curr", required = false, defaultValue = "1") Integer page,
                                       @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
        return AjaxResult.success(sysMovieService.getMovieListPage(page, size));
    }


//     use movie_recommend_big
//     db.ratings.ensureIndex({userId: 1, movieId: 1}, {background: true})

    @ApiOperation("获取当前用户对该电影的评分")
    @GetMapping("/rating/{mid}")
    public AjaxResult checkUserRating(@PathVariable("mid") Long movieId){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        Long userId = user.getUserId();
        MongoCollection<Document> mongoCollection =
                mongoClient.getDatabase(MongoConstants.MONGODB_INPUT).getCollection(MongoConstants.MONGODB_RATING_COLLECTION);
        Document document = mongoCollection.find(Filters.and(Filters.eq("userId", userId), Filters.eq("movieId", movieId))).first();
        if (StringUtils.isNull(document)){
            return AjaxResult.success(0);
        }
        return AjaxResult.success(document.getDouble("rating"));
    }

    @ApiOperation("更新当前用户对该电影的评分")
    @PutMapping("/rating/{mid}")
    public AjaxResult updateUserRating(@PathVariable("mid") Long movieId, @RequestParam("rating") Double rating){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        Long userId = user.getUserId();
        MongoCollection<Document> mongoCollection =
                mongoClient.getDatabase(MongoConstants.MONGODB_INPUT).getCollection(MongoConstants.MONGODB_RATING_COLLECTION);
        mongoCollection.findOneAndDelete(Filters.and(Filters.eq("userId", userId), Filters.eq("movieId", movieId)));
        InsertOneResult insertOneResult = mongoCollection.insertOne(new Document().append("userId", userId).append("movieId", movieId).append("rating", rating).append("timestamp", System.currentTimeMillis()));
        return AjaxResult.success(insertOneResult.wasAcknowledged());
    }
}