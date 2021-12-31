package com.yankaizhang.movielikes.srv.controller;

import com.yankaizhang.movielikes.srv.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Api("用户注册")
@RequestMapping("/register")
@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    public void setUsers(String name,String password){
        userService.setUser(name,password);
    }
}
