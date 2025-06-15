package com.example.OnlineMovieStreamingSystem.dto.request.userInteraction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInteractionRequestDTO {
    private double ratingValue;
    private long movieId;
}
