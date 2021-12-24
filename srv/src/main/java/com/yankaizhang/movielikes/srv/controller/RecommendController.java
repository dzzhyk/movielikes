package com.yankaizhang.movielikes.srv.controller;

import com.yankaizhang.movielikes.srv.entity.Movie;
import com.yankaizhang.movielikes.srv.entity.Recommendation;
import com.yankaizhang.movielikes.srv.service.DataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("电影推荐接口")
@RequestMapping("/recommend")
@Controller
public class RecommendController {

    private final DataService dataService;

    @Autowired
    public RecommendController(DataService dataService) {
        this.dataService = dataService;
    }

    @ApiOperation("获取热门电影榜单")
    @GetMapping("/hot")
    @ResponseBody
    public List<Movie> getHotMovies() {
        List<Recommendation> recommendations = dataService.getHotRecommendations(5);
        return dataService.getRecommendMovies(recommendations);
    }

    @ApiOperation("评分最多电影榜单")
    @GetMapping("/rate")
    @ResponseBody
    public List<Movie> getRateMoreMovies() {
        List<Recommendation> recommendations = dataService.getRateMoreRecommendations(5);
        return dataService.getRecommendMovies(recommendations);
    }

    @ApiOperation("用户个性推荐榜单")
    @GetMapping("/user")
    @ResponseBody
    public List<Movie> getUserRecommendMovies(Integer userId) {
        return dataService.getUserRecommend(userId);
    }


}
