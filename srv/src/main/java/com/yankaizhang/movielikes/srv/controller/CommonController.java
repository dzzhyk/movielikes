package com.yankaizhang.movielikes.srv.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dzzhyk
 */
@Api("通用接口")
@RestController
public class CommonController {

    @ApiOperation("主页")
    @GetMapping({"/", "/index", "/home"})
    public String index(){
        return "index";
    }

    @ApiOperation("电影详情页")
    @GetMapping({"/movie/{movieId}"})
    public String detail(@PathVariable("movieId") Integer movieId){
        return "detail";
    }

    @ApiOperation("个人收藏页")
    @GetMapping("/collect")
    public String collection(){
        return "collect";
    }

}
