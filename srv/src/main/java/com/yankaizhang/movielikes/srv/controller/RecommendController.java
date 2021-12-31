package com.yankaizhang.movielikes.srv.controller;

import com.yankaizhang.movielikes.srv.api.AjaxResult;
import com.yankaizhang.movielikes.srv.entity.vo.MovieVO;
import com.yankaizhang.movielikes.srv.service.IRecommendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 电影推荐接口
 */
@Api("电影推荐接口")
@RequestMapping("/recommend")
@RestController
public class RecommendController {

    @Autowired
    private IRecommendService recommendService;

    @ApiOperation("评分最多电影")
    @GetMapping("/most")
    public AjaxResult getMostRated() {
        return AjaxResult.success(recommendService.getMostRated());
    }

    @ApiOperation("最近热门电影")
    @GetMapping("/rank")
    public AjaxResult getRecentlyHot() {
        return AjaxResult.success(recommendService.getMostRatedRecently());
    }


    @ApiOperation("用户个性推荐")
    @GetMapping("/user/{uid}")
    public AjaxResult getUserRecommend(@PathVariable("uid") Long userId) {
        List<MovieVO> userRecommend = recommendService.getUserRecommend(userId);
        return AjaxResult.success(userRecommend);
    }

}
