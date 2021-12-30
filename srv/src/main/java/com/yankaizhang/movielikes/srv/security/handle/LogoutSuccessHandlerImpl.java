package com.yankaizhang.movielikes.srv.security.handle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yankaizhang.movielikes.srv.api.AjaxResult;
import com.yankaizhang.movielikes.srv.constant.HttpStatus;
import com.yankaizhang.movielikes.srv.security.LoginUser;
import com.yankaizhang.movielikes.srv.security.service.TokenService;
import com.yankaizhang.movielikes.srv.util.ServletUtils;
import com.yankaizhang.movielikes.srv.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义退出处理类 返回成功
 *
 * @author ruoyi
 */
@Slf4j
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 退出处理
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        LoginUser loginUser = tokenService.getLoginUser(request);
        log.info(String.valueOf(loginUser));
        if (StringUtils.isNotNull(loginUser)) {
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
            log.info("删除用户缓存记录");
        }
        ServletUtils.renderString(response, objectMapper.writeValueAsString(AjaxResult.error(HttpStatus.SUCCESS, "退出成功")));
    }
}
