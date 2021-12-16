package com.yankaizhang.movielikes.srv.Service;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.util.JSON;
import com.yankaizhang.movielikes.srv.Entity.Movie;
import com.yankaizhang.movielikes.srv.HotRecommendation;
import com.yankaizhang.movielikes.srv.Recommendation;
import com.yankaizhang.movielikes.srv.Utils.CollectionName;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataService {
    private final MongoClient mongoClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public DataService(MongoClient mongoClient, ObjectMapper objectMapper) {
        this.mongoClient = mongoClient;
        this.objectMapper = objectMapper;
    }

    private MongoCollection<Document> movieCollection;
    private MongoCollection<Document> averageMoviesScoreCollection;

    private MongoCollection<Document> getMovieCollection() {
        if (null == movieCollection)
            movieCollection = mongoClient.getDatabase(CollectionName.MONGODB_DATABASE).getCollection(CollectionName.MONGODB_MOVIE_COLLECTION);
        return movieCollection;
    }

    private MongoCollection<Document> getAverageMoviesScoreCollection() {
        if (null == averageMoviesScoreCollection)
            averageMoviesScoreCollection = mongoClient.getDatabase(CollectionName.MONGODB_DATABASE).getCollection(CollectionName.MONGODB_AVERAGE_MOVIES_SCORE_COLLECTION);
        return averageMoviesScoreCollection;
    }

    public List<Recommendation> getHotRecommendations(HotRecommendation hotRecommendation) {
        MongoCollection<Document> rateMoreMoviesRecentlyCollection = mongoClient.getDatabase(CollectionName.MONGODB_DATABASE).getCollection(CollectionName.MONGODB_RATE_MORE_MOVIES_RECENTLY_COLLECTION);
        FindIterable<Document> documents = rateMoreMoviesRecentlyCollection.find().sort(Sorts.descending("yearmonth")).limit(hotRecommendation.getSum());

        List<Recommendation> recommendations = new ArrayList<>();
        for (Document document : documents) {
            recommendations.add(new Recommendation(document.getInteger("movieId"), 0D));
        }
        return recommendations;
    }

    public List<Movie> getRecommendMovies(List<Recommendation> recommendations) {
        List<Integer> ids = new ArrayList<>();
        for (Recommendation rec : recommendations) {
            ids.add(rec.getMovieId());
        }
        return getMovies(ids);
    }

    public List<Movie> getMovies(List<Integer> mids) {
        FindIterable<Document> documents = getMovieCollection().find(Filters.in("movieId", mids));
        List<Movie> movies = new ArrayList<>();
        for (Document document : documents) {
            movies.add(documentToMovie(document));
        }
        return movies;
    }

    private Movie documentToMovie(Document document) {
        Movie movie = null;
        try {
            movie = objectMapper.readValue(JSON.serialize(document), Movie.class);
            Document score = getAverageMoviesScoreCollection().find(Filters.eq("movieId", movie.getMovieId())).first();
            if (null == score || score.isEmpty())
                movie.setScore(0D);
            else
                movie.setScore(score.get("avg", 0D));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movie;
    }
}
