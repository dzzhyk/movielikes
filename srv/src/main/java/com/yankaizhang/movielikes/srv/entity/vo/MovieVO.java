package com.yankaizhang.movielikes.srv.entity.vo;

import com.yankaizhang.movielikes.srv.entity.SysMovie;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Movie推荐响应体
 *
 * @author dzzhyk
 */
@Data
@NoArgsConstructor
public class MovieVO implements Serializable {

    public MovieVO(SysMovie sysMovie, String avgRating){
        this.movieId = sysMovie.getMovieId();
        this.title = sysMovie.getTitle();
        this.release = sysMovie.getRelease();
        this.posterPath = sysMovie.getPosterPath();
        this.avgRating = avgRating;
    }

    private Long movieId;
    private String title;
    private String release;
    private String posterPath;
    private String avgRating;

}
