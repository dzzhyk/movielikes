package com.yankaizhang.movielikes.srv.service;

import com.yankaizhang.movielikes.srv.entity.SysMovie;
import com.yankaizhang.movielikes.srv.entity.vo.MovieVO;

import java.util.List;
import java.util.Map;

/**
 * 推荐服务接口
 * @author dzzhyk
 */
public interface IRecommendService {


    /**
     * 获取历史热门推荐
     * @return 电影列表
     */
    List<MovieVO> getMostRated();

    /**
     * 获取近期热门推荐
     * @return 电影列表
     */
    List<MovieVO> getMostRatedRecently();

    /**
     * 获取用户个人推荐
     * @param userId 用户id
     * @return 推荐结果电影列表
     */
    List<MovieVO> getUserRecommend(Long userId);

    /**
     * 加载平均rating
     * @return 平均ratings
     */
    Map<String, String> getAvgRatings();
}
