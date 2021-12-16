package com.yankaizhang.movielikes.srv;
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
