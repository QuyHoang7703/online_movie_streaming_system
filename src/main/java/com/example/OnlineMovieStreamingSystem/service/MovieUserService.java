package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.recommendMovie.RecommendationMovieRequest;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieUserResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.SubscriptionPlanResponseDTO;
import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;

import java.util.List;

public interface MovieUserService {
    boolean canUserWatchMovie(long movieId);
    List<SubscriptionPlanResponseDTO> getSubscriptionPlansForMovie(long movieId);
    List<MovieUserResponseDTO> getRecommendationsForMovie(RecommendationMovieRequest recommendationMovieRequest);
    ResultPaginationDTO getHotMovieByMovieType(MovieType movieType,String country, int size);
}
