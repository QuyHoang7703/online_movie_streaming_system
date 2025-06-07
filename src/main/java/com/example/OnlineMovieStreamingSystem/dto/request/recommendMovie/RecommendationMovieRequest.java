package com.example.OnlineMovieStreamingSystem.dto.request.recommendMovie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationMovieRequest {
    private String title;
    private int numRecommendations;
    // weight for CBF + NeuMF
    private double cbfWeight;
    private double neumfWeight;
}
