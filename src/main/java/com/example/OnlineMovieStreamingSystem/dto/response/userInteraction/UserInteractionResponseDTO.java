package com.example.OnlineMovieStreamingSystem.dto.response.userInteraction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInteractionResponseDTO {
    private long userId;
    private String email;
    private double ratingValue;
    private long movieId;
}
