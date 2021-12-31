package com.yankaizhang.movielikes.srv.entity;
import lombok.Data;

@Data
public class Recommendation {

    public Recommendation(int mid, Double score) {
        this.movieId = mid;
        this.score = score;
    }

    private int movieId;
    private Double score;
}
