package com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanDurationResponseDTO {
    private long id;
    private String name;
    private double price;
    private int durationInMonths;
}
