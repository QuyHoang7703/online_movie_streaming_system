package com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPlanSummaryDTO {
    private long id;
    private String name;
}
