package com.yankaizhang.movielikes.srv.Entity;

import lombok.Data;

@Data
public class Movie {
    private Integer movieId;
    private String title;
    private String genres;
    private Double score;
}
