package com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPlanResponseDTO {
    private long id;
    private String name;
    private String description;
    private List<String> features;
    private boolean isActive;
    private List<PlanDurationResponseDTO> planDurations;
    private List<SubscriptionPlanSummaryDTO> parentPlans;
    private List<SubscriptionPlanSummaryDTO> childPlans;

}
