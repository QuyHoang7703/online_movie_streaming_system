package com.example.OnlineMovieStreamingSystem.dto.response.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieActorResponseDTO {
    private long actorId;
    private String actorName;
    private String avatarUrl;
    private String characterName;
}
