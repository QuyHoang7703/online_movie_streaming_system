package com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPlanSummaryDTO {
    private long id;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Long> parentIds;
}
