package com.yankaizhang.movielikes.srv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;


@EnableOpenApi
@SpringBootApplication
public class MovielikesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovielikesApplication.class, args);
    }

}
