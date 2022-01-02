package com.yankaizhang.movielikes.srv.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户和电影关联表
 * </p>
 *
 * @author dzzhyk
 * @since 2021-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_user_movie")
public class SysUserMovie implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
//    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 电影ID
     */
//    @TableId(value = "movie_id", type = IdType.AUTO)
    private Long movieId;


}
