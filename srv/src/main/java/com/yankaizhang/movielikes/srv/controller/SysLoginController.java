package com.yankaizhang.movielikes.srv.controller;

import com.yankaizhang.movielikes.srv.api.AjaxResult;
import com.yankaizhang.movielikes.srv.constant.Constants;
import com.yankaizhang.movielikes.srv.entity.SysUser;
import com.yankaizhang.movielikes.srv.exception.ServiceException;
import com.yankaizhang.movielikes.srv.interceptor.annotation.RepeatSubmit;
import com.yankaizhang.movielikes.srv.security.LoginBody;
import com.yankaizhang.movielikes.srv.security.service.SysLoginService;
import com.yankaizhang.movielikes.srv.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录验证
 */
@Api("登录接口")
@Slf4j
@RestController
public class SysLoginController {
    @Autowired
    private SysLoginService loginService;

    @ApiOperation("登录方法")
    @RepeatSubmit
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody) {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(), loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/getInfo")
    public AjaxResult getInfo() {
        SysUser user;
        AjaxResult ajax;
        try {
            user = SecurityUtils.getLoginUser().getUser();
            log.info("获取当前登录的用户: {}", user);
            ajax = AjaxResult.success();
            ajax.put("user", user);
        } catch (ServiceException e) {
            ajax = AjaxResult.error("请登录");
        }
        return ajax;
    }
}
