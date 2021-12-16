package com.yankaizhang.movielikes.srv;

import lombok.Data;

@Data
public class HotRecommendation {
    private int sum;
    public HotRecommendation(int sum) {
        this.sum = sum;
    }
}
