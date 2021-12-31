package com.yankaizhang.movielikes.srv.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import com.yankaizhang.movielikes.srv.constant.MongoConstant;
import com.yankaizhang.movielikes.srv.entity.Movie;
import com.yankaizhang.movielikes.srv.entity.User;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.io.IOException;

@SuppressWarnings("ALL")
@Service
public class UserService {
    private final MongoClient mongoClient;
    private final ObjectMapper objectMapper;
    @Autowired
    public UserService(MongoClient mongoClient, ObjectMapper objectMapper) {
        this.mongoClient = mongoClient;
        this.objectMapper = objectMapper;
    }
    private MongoCollection<Document> userCollection;
    private MongoCollection<Document> getUserCollection() {
        if (null == userCollection) {
            userCollection = mongoClient.getDatabase(MongoConstant.MONGODB_INPUT).getCollection(MongoConstant.MONGODB_USER_COLLECTION);
        }
        return userCollection;
    }
    public boolean setUser(String name,String password){
        //判断用户是否存在并插入
        if(getUser(name)!=null){
            User user=new User();
            user.setName(name);
            user.setPassword(password);
            String json = JSON.serialize(user);
            Document document = Document.parse(json);
            getUserCollection().insertOne(document);
            return true;
        }
        return false;
    }
    public User getUser(String name){
        User user=null;
        try {
            Document document=getUserCollection().find(Filters.eq("userId", name)).first();
            if (null == document || document.isEmpty()) {
                return null;
            } else {
                user = objectMapper.readValue(JSON.serialize(document), User.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }
}
