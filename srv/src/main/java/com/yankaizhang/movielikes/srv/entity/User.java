package com.yankaizhang.movielikes.srv.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class User {

    @JsonIgnore
    private int uid;

    private String  username;

    private String  password;

    private List<String> genres = new ArrayList<>();

    private long timeStamp;

    private boolean first;




    public void setUsername(String username) {
        this.uid = username.hashCode();
        this.username = username;
    }


    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public boolean passwordMatch(String password) {
        return this.password.compareTo(password) == 0;
    }


    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }


    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
