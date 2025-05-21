package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.SubscriptionPlanResponseDTO;

import java.util.List;

public interface MovieUserService {
    boolean canUserWatchMovie(long movieId);
    List<SubscriptionPlanResponseDTO> getSubscriptionPlansForMovie(long movieId);
}
