package com.yankaizhang.movielikes.srv.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yankaizhang.movielikes.srv.api.AjaxResult;
import com.yankaizhang.movielikes.srv.entity.SysUser;
import com.yankaizhang.movielikes.srv.entity.SysUserMovie;
import com.yankaizhang.movielikes.srv.exception.ServiceException;
import com.yankaizhang.movielikes.srv.mapper.SysUserMapper;
import com.yankaizhang.movielikes.srv.mapper.SysUserMovieMapper;
import com.yankaizhang.movielikes.srv.service.ISysUserMovieService;
import com.yankaizhang.movielikes.srv.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息接口
 * @author dzzhyk
 */
@Api("用户信息接口")
@Slf4j
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserMovieMapper userMovieMapper;

    @ApiOperation("获取用户收藏")
    @GetMapping("/collection")
    public AjaxResult getUserCollection(){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        Long userId = user.getUserId();
        List<SysUserMovie> userCollections
                = userMovieMapper.selectList(new QueryWrapper<SysUserMovie>().eq("user_id", userId));
        List<Long> list = userCollections.stream().map(SysUserMovie::getMovieId).collect(Collectors.toList());
        return AjaxResult.success(list);
    }

}
