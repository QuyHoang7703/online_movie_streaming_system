package com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionPlanTreeDTO {
    private long id;
    private String name;
    private List<SubscriptionPlanTreeDTO> children;
}
