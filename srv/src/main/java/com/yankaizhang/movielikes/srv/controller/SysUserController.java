package com.yankaizhang.movielikes.srv.controller;

import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yankaizhang.movielikes.srv.api.AjaxResult;
import com.yankaizhang.movielikes.srv.entity.SysUser;
import com.yankaizhang.movielikes.srv.entity.SysUserMovie;
import com.yankaizhang.movielikes.srv.entity.vo.UserVO;
import com.yankaizhang.movielikes.srv.exception.ServiceException;
import com.yankaizhang.movielikes.srv.mapper.SysUserMapper;
import com.yankaizhang.movielikes.srv.mapper.SysUserMovieMapper;
import com.yankaizhang.movielikes.srv.security.LoginUser;
import com.yankaizhang.movielikes.srv.security.service.TokenService;
import com.yankaizhang.movielikes.srv.service.ISysUserMovieService;
import com.yankaizhang.movielikes.srv.service.ISysUserService;
import com.yankaizhang.movielikes.srv.util.SecurityUtils;
import com.yankaizhang.movielikes.srv.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息接口
 *
 * @author dzzhyk
 */
@Api("用户信息接口")
@Slf4j
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserMovieMapper userMovieMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private TokenService tokenService;

    @ApiOperation("获取用户收藏")
    @GetMapping("/collection")
    public AjaxResult getUserCollection() {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        Long userId = user.getUserId();
        List<SysUserMovie> userCollections
                = userMovieMapper.selectList(new QueryWrapper<SysUserMovie>().eq("user_id", userId));
        List<Long> list = userCollections.stream().map(SysUserMovie::getMovieId).collect(Collectors.toList());
        return AjaxResult.success(list);
    }

    @ApiOperation("更新用户资料")
    @PutMapping("/profile")
    public AjaxResult updateUserProfile(@RequestBody UserVO userVO) {
        if (StringUtils.isNull(userVO)) {
            return AjaxResult.error();
        } else {
            String userVOUsername = userVO.getUsername();
            String userVOEmail = userVO.getEmail();
            if (StringUtils.isNull(userVOUsername) || StringUtils.isEmpty(userVOUsername) || !ReUtil.isMatch("^[a-zA-Z][a-zA-Z0-9_]{2,20}$", userVOUsername)) {
                return AjaxResult.error("账户名不合法");
            }
            if (StringUtils.isNull(userVOEmail) || StringUtils.isEmpty(userVOEmail) || !ReUtil.isMatch("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", userVOEmail)) {
                return AjaxResult.error("邮箱不合法");
            }
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser user = loginUser.getUser();
            user.setUserName(userVOUsername);
            user.setEmail(userVOEmail);
            user.setPassword(null);
            if (userMapper.updateById(user) > 0) {
                // 更新缓存用户信息
                tokenService.setLoginUser(loginUser);
                return AjaxResult.success();
            }
            return AjaxResult.error("修改个人信息异常，请联系管理员");
        }
    }

    @ApiOperation("更新用户密码")
    @PutMapping("/pwd")
    public AjaxResult updateUserPassword(String newPassword) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getUsername();
        String password = loginUser.getPassword();
        if (SecurityUtils.matchesPassword(newPassword, password)) {
            return AjaxResult.error("新密码不能与旧密码相同");
        }
        if (userMapper.resetUserPwd(userName, SecurityUtils.encryptPassword(newPassword)) > 0) {
            // 更新缓存用户密码
            loginUser.getUser().setPassword(SecurityUtils.encryptPassword(newPassword));
            tokenService.setLoginUser(loginUser);
            return AjaxResult.success();
        }
        return AjaxResult.error("修改密码异常，请联系管理员");
    }

    @ApiOperation("添加用户收藏")
    @PostMapping("/collection/{mid}")
    public AjaxResult addUserCollection(@PathVariable("mid") Long movieId) {
        SysUserMovie sysUserMovie = new SysUserMovie();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        Long userId = user.getUserId();
        sysUserMovie.setUserId(userId);
        sysUserMovie.setMovieId(movieId);
        int insert = userMovieMapper.insert(sysUserMovie);
        return AjaxResult.success(insert);
    }

    @ApiOperation("删除用户收藏")
    @DeleteMapping("/collection")
    public AjaxResult deleteUserCollection(@PathVariable("mid") Long movieId) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        Long userId = user.getUserId();
        int delete = userMovieMapper.delete(new QueryWrapper<SysUserMovie>().eq("user_id", userId).eq("movie_id", movieId));
        return AjaxResult.success(delete);
    }

}
