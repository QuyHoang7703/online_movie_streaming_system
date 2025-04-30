package com.example.OnlineMovieStreamingSystem.dto.request.subscriptionPlan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanDurationRequestDTO {
    private long id;
    private String name;
    private double price;
    private int durationInMonths;
}
