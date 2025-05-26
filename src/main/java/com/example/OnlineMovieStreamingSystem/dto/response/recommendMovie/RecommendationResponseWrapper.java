package com.example.OnlineMovieStreamingSystem.dto.response.recommendMovie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationResponseWrapper {
    private RecommendationResponseData data;
}
