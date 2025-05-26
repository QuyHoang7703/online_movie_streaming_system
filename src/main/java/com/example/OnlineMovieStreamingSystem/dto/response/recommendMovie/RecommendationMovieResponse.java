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
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RecommendationMovieResponse {
    private long id;
    private String title;
    private double voteAverage;
    private double voteCount;
    private double wr;
    private int year;
}
