package com.example.OnlineMovieStreamingSystem.dto.request.recommendMovie;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RecommendationMovieRequest {
    private String title;
    // weight for CBF + NeuMF => Hybrid
    private long user_id;
    private long tmdb_id;
    private int num_recommendations;
    private double cbf_weight;
    private double neumf_weight;
}
