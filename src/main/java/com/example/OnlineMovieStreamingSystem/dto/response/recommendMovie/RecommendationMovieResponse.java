package com.example.OnlineMovieStreamingSystem.dto.response.recommendMovie;

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
public class RecommendationMovieResponse {
    private long movie_id;
    private long tmdb_id;
    private String title;
    private double cbf_score;
    private double neumf_score;
    private double hybrid_score;
    private double predicted_score;

    private double vote_average;
    private double vote_count;
    private String source;


    private int year;
}
