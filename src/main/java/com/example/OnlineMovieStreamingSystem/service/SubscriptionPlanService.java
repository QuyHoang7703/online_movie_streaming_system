package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.domain.SubscriptionPlan;
import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.subscriptionPlan.SubscriptionPlanRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.SubscriptionPlanResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.SubscriptionPlanSummaryDTO;

import java.util.List;

public interface SubscriptionPlanService {
    SubscriptionPlanResponseDTO createSubscriptionPlan(SubscriptionPlanRequestDTO subscriptionPlanRequestDTO);
    SubscriptionPlanResponseDTO getSubscriptionPlanById(long subscriptionPlanId);
    ResultPaginationDTO getSubscriptionPlans(int page, int size);
    List<SubscriptionPlanSummaryDTO> getSubscriptionPlanOptions(Long subscriptionPlanId);
    SubscriptionPlanResponseDTO updateSubscriptionPlan(long subscriptionPlanId, SubscriptionPlanRequestDTO subscriptionPlanRequestDTO);
    void deleteSubscriptionPlan(long subscriptionPlanId);
    SubscriptionPlanSummaryDTO convertToSubscriptionPlanSummaryDTO(SubscriptionPlan subscriptionPlan);
    SubscriptionPlanResponseDTO convertToSubscriptionPlanResponseDTO(SubscriptionPlan subscriptionPlan);
}
