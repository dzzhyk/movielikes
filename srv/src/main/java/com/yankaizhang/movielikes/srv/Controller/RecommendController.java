package com.yankaizhang.movielikes.srv.Controller;

import com.yankaizhang.movielikes.srv.Recommendation;
import com.yankaizhang.movielikes.srv.Service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class RecommendController {

    private final DataService dataService;

    @Autowired
    public RecommendController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/recommend/hot")
    @ResponseBody
    public List getHotMovies() {
        List<Recommendation> recommendations = dataService.getHotRecommendations(5);

        return dataService.getRecommendMovies(recommendations);
    }
}
