package com.yankaizhang.movielikes.srv.controller;

import com.yankaizhang.movielikes.srv.entity.Movie;
import com.yankaizhang.movielikes.srv.entity.Recommendation;
import com.yankaizhang.movielikes.srv.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/recommend")
@Controller
public class RecommendController {

    private final DataService dataService;

    @Autowired
    public RecommendController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/hot")
    @ResponseBody
    public List<Movie>  getHotMovies() {
        List<Recommendation> recommendations = dataService.getHotRecommendations(5);
        return dataService.getRecommendMovies(recommendations);
    }

    @GetMapping("/rate")
    @ResponseBody
    public List<Movie>  getRateMoreMovies() {
        List<Recommendation> recommendations = dataService.getRateMoreRecommendations(5);
        return dataService.getRecommendMovies(recommendations);
    }

    @GetMapping("/user")
    @ResponseBody
    public List<Movie> getUserRecommendMovies(Integer userId) {
        return dataService.userRecommend(userId);
    }


}
