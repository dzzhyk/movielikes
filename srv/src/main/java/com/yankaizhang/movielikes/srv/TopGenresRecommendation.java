package com.yankaizhang.movielikes.srv;

import lombok.Data;

@Data
public class TopGenresRecommendation {
    private int sum;
    private String genres;

    public TopGenresRecommendation(String genres, int sum) {
        this.genres = genres;
        this.sum = sum;
    }
}
