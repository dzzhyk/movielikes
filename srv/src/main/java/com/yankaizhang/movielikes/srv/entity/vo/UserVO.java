package com.yankaizhang.movielikes.srv.entity.vo;

import lombok.Data;

/**
 * @author dzzhyk
 */
@Data
public class UserVO {

    public UserVO(String username, String email) {
        this.username = username;
        this.email = email;
    }

    private String username;
    private String email;

}
