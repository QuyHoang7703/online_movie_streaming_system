package com.example.OnlineMovieStreamingSystem.dto.request.subscriptionPlan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPlanRequestDTO {
    private String name;
    private String description;
    private List<String> features;
    private List<Long> parentPlanIds;
    private boolean isActive;
    private List<PlanDurationRequestDTO> planDurations;

}
