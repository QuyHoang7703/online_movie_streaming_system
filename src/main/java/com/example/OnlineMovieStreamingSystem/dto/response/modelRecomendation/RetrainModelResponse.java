package com.example.OnlineMovieStreamingSystem.dto.response.modelRecomendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetrainModelResponse {
    private boolean success;
    private String dataset_name;
    private double total_ratings;
    private int new_ratings_count;
    private int new_movies_count;
    private long timestamp;

}
