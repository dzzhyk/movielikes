package com.yankaizhang.movielikes.srv.controller;

import com.yankaizhang.movielikes.srv.api.AjaxResult;
import com.yankaizhang.movielikes.srv.interceptor.annotation.RepeatSubmit;
import com.yankaizhang.movielikes.srv.security.RegisterBody;
import com.yankaizhang.movielikes.srv.security.service.SysRegisterService;
import com.yankaizhang.movielikes.srv.util.StringUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册验证
 */
@Api("注册接口")
@RestController
public class SysRegisterController
{
    @Autowired
    private SysRegisterService registerService;

    @RepeatSubmit
    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterBody user)
    {
        String msg = registerService.register(user);
        return StringUtils.isEmpty(msg) ? AjaxResult.success() : AjaxResult.error(msg);
    }
}
