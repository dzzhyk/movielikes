package com.yankaizhang.movielikes.srv.service;

import com.yankaizhang.movielikes.srv.entity.SysMovie;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 电影表 服务类
 * </p>
 *
 * @author dzzhyk
 * @since 2021-12-28
 */
public interface ISysMovieService extends IService<SysMovie> {

    /**
     * 获取电影详细信息
     * @param movieId 电影id
     * @return 电影详细信息
     */
    Map<String, Object> getMovieDetails(Long movieId);
}
