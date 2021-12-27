package com.yankaizhang.movielikes.srv.controller;

import com.yankaizhang.movielikes.srv.entity.User;
import com.yankaizhang.movielikes.srv.entity.request.LoginUserRequest;
import com.yankaizhang.movielikes.srv.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;

@Controller
@RequestMapping("/users")


public class UserRestAPI {

    private UserService userService;

    //register
    @RequestMapping(path ="/register",method = RequestMethod.GET)
    @ResponseBody
    public Model registerUser(@RequestParam("username") String username ,
                              @RequestParam("password") String password, Model model){

        User user = new User();
        user.setUsername(username);
        model.addAttribute( "success",false);
        model.addAttribute( "message","用户已注册");
        return model;
    }


    //login
    @RequestMapping(path ="/login",method = RequestMethod.GET)
    @ResponseBody
    public Model loginUser(@RequestParam("username")String username ,  @RequestParam("password") String password, Model model){
        User user = userService.loginUser(new LoginUserRequest(username,password));
        model.addAttribute("success",user != null);
        model.addAttribute("user",user);
        return  null;
    }


    @RequestMapping(value = "/pref", method = RequestMethod.GET)
    @ResponseBody
    public Model addPrefGenres(@RequestParam("username") String username,@RequestParam("genres") String genres,Model model) {
        User user = userService.findByUsername(username);
        user.getGenres().addAll(Arrays.asList(genres.split(",")));
        user.setFirst(false);
        model.addAttribute("success",userService.updateUser(user));
        return model;
    }



}
