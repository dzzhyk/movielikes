package com.yankaizhang.movielikes.srv.controller;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author dzzhyk
 */
@Api("通用接口")
@Controller
public class CommonController {

    @GetMapping({"/", "/index", "/home"})
    public String index(){
        return "index";
    }

}
