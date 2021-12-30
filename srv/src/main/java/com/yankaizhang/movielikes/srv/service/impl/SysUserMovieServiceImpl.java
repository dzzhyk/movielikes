package com.yankaizhang.movielikes.srv.service.impl;

import com.yankaizhang.movielikes.srv.entity.SysUserMovie;
import com.yankaizhang.movielikes.srv.mapper.SysUserMovieMapper;
import com.yankaizhang.movielikes.srv.service.ISysUserMovieService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户和电影关联表 服务实现类
 * </p>
 *
 * @author dzzhyk
 * @since 2021-12-28
 */
@Service
public class SysUserMovieServiceImpl extends ServiceImpl<SysUserMovieMapper, SysUserMovie> implements ISysUserMovieService {

}
