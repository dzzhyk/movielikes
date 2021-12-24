package com.yankaizhang.movielikes.srv.service;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.util.JSON;
import com.yankaizhang.movielikes.srv.entity.Movie;
import com.yankaizhang.movielikes.srv.entity.Recommendation;
import com.yankaizhang.movielikes.srv.entity.TopGenresRecommendation;
import com.yankaizhang.movielikes.srv.util.CollectionName;
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
            movieCollection = mongoClient.getDatabase(CollectionName.MONGODB_INPUT).getCollection(CollectionName.MONGODB_MOVIE_COLLECTION);
        return movieCollection;
    }

    private MongoCollection<Document> getAverageMoviesScoreCollection() {
        if (null == averageMoviesScoreCollection)
            averageMoviesScoreCollection = mongoClient.getDatabase(CollectionName.MONGODB_DATABASE).getCollection(CollectionName.MONGODB_AVERAGE_MOVIES_SCORE_COLLECTION);
        return averageMoviesScoreCollection;
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

    public List<Recommendation> getHotRecommendations(Integer num) {
        MongoCollection<Document> rateMoreMoviesRecentlyCollection = mongoClient.getDatabase(CollectionName.MONGODB_DATABASE).getCollection(CollectionName.MONGODB_RATE_MORE_MOVIES_RECENTLY_COLLECTION);
        FindIterable<Document> documents = rateMoreMoviesRecentlyCollection.find().sort(Sorts.descending("yearmonth")).limit(num);

        List<Recommendation> recommendations = new ArrayList<>();
        for (Document document : documents) {
            recommendations.add(new Recommendation(document.getInteger("movieId"), 0D));
        }
        return recommendations;
    }

    public List<Recommendation> getRateMoreRecommendations(Integer num) {

        MongoCollection<Document> rateMoreMoviesCollection = mongoClient.getDatabase(CollectionName.MONGODB_DATABASE).getCollection(CollectionName.MONGODB_RATE_MORE_MOVIES_COLLECTION);
        FindIterable<Document> documents = rateMoreMoviesCollection.find().sort(Sorts.descending("count")).limit(num);
        List<Recommendation> recommendations = new ArrayList<>();
        for (Document document : documents) {
            recommendations.add(new Recommendation(document.getInteger("movieId"), 0D));
        }

        return recommendations;
    }

    private List<Recommendation> exchange(Document document, int maxItems) {
        List<Recommendation> recommendations = new ArrayList<>();
        if (null == document || document.isEmpty())
            return recommendations;
        ArrayList<Document> recs = document.get("recs", ArrayList.class);
        for (Document recDoc : recs) {
            recommendations.add(new Recommendation(recDoc.getInteger("movieId"), recDoc.getDouble("score")));
        }

        recommendations.sort((o1, o2) -> o1.getScore() > o2.getScore() ? -1 : 1);
        return recommendations.subList(0, Math.min(maxItems, recommendations.size()));
    }

    public List<Recommendation> getTopGenresRecommendations(TopGenresRecommendation topGenresRecommendation){
        Document genresTopMovies = mongoClient.getDatabase(CollectionName.MONGODB_DATABASE).getCollection(CollectionName.MONGODB_GENRES_TOP_MOVIES_COLLECTION)
                .find(Filters.eq("genres",topGenresRecommendation.getGenres())).first();
        return exchange(genresTopMovies,topGenresRecommendation.getSum());
    }

    public List<Movie> userRecommend(Integer userId) {
        Document document =mongoClient.getDatabase(CollectionName.MONGODB_DATABASE).getCollection(CollectionName.MONGODB_ITEMCF_RESULT_BIG)
                .find(Filters.eq("userId",userId)).first();
        List<Integer>recommendations = new ArrayList<>();
        ArrayList<Document> recs=document.get("recommendations", ArrayList.class);
        for(Document rec: recs) {
            recommendations.add(rec.getInteger("movieId"));
        }

        return getMovies(recommendations);
    }

}
