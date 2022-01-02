package com.yankaizhang.movielikes.srv.controller;

import com.yankaizhang.movielikes.srv.api.AjaxResult;
import com.yankaizhang.movielikes.srv.service.ISysMovieService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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

    @ApiOperation("获取电影详情")
    @GetMapping("/detail/{mid}")
    public AjaxResult getMovieDetails(@PathVariable("mid") Long movieId) {
        Map<String, Object> result = sysMovieService.getMovieDetails(movieId);
        return AjaxResult.success(result);
    }

    @ApiOperation("分页获取电影")
    @GetMapping("/list")
    public AjaxResult getMovieListPage(@RequestParam(value = "curr", required = false, defaultValue = "1") Integer page,
                                       @RequestParam(value = "size", required = false, defaultValue = "20") Integer size
    ) {
        log.info("{}, {}", page, size);
        return AjaxResult.success(sysMovieService.getMovieListPage(page, size));
    }

}
