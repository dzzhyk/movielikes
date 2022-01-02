package com.yankaizhang.movielikes.srv.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 电影表
 * </p>
 *
 * @author dzzhyk
 * @since 2021-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_movie")
public class SysMovie implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 电影ID
     */
    @TableId(value = "movie_id", type = IdType.AUTO)
    private Long movieId;

    /**
     * 电影名称
     */
    private String title;

    /**
     * 电影分类
     */
    private String genres;

    /**
     * IMDB ID
     */
    private String imdbId;

    /**
     * TMDB ID
     */
    private String tmdbId;

    /**
     * 上映时间
     */
    @TableField("`release`")
    private String release;

    /**
     * 总时长(分钟)
     */
    private Integer runtime;

    /**
     * 简介
     */
    private String overview;

    /**
     * 海报uri
     */
    private String posterPath;


}
