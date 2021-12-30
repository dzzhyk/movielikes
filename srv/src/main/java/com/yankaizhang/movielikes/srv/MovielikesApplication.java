package com.yankaizhang.movielikes.srv;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;


@EnableOpenApi
@SpringBootApplication
@MapperScan("com.yankaizhang.movielikes.srv.mapper")
public class MovielikesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovielikesApplication.class, args);
    }

}
