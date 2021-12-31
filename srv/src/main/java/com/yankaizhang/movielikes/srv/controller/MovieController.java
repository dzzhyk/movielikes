package com.yankaizhang.movielikes.srv.controller;

import com.yankaizhang.movielikes.srv.api.AjaxResult;
import com.yankaizhang.movielikes.srv.service.ISysMovieService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 电影接口
 * @author dzzhyk
 */
@Api("电影接口")
@Slf4j
@RestController
@RequestMapping("/movie")
public class MovieController {

    @Autowired
    private ISysMovieService sysMovieService;

    @ApiOperation("获取电影详情")
    @GetMapping("/detail/{mid}")
    public AjaxResult getMovieDetails(@PathVariable("mid") Long movieId){
        Map<String, Object> result = sysMovieService.getMovieDetails(movieId);
        return AjaxResult.success(result);
    }

}
