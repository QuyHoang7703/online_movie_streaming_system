package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.request.subscriptionPlan.SubscriptionPlanRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.SubscriptionPlanResponseDTO;

public interface SubscriptionService {
    SubscriptionPlanResponseDTO createSubscriptionPlan(SubscriptionPlanRequestDTO subscriptionPlanRequestDTO);
    SubscriptionPlanResponseDTO getSubscriptionPlan(long subscriptionPlanId);
}
