package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;

import java.util.List;

public interface MovieRedisService {
    String getKey(List<String> genreNames,
                  String movieType,
                  List<String> countries,
                  Long subscriptionPlanId,
                  int page,
                  int size,
                  boolean isAdmin);

    void clearMovieInRedis();



}
