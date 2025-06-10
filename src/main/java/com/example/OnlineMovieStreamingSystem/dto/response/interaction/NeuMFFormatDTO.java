package com.example.OnlineMovieStreamingSystem.dto.response.interaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NeuMFFormatDTO {
    private long userId;
    private long movieId;
    private double rating;
    private Instant timestamp;
}
