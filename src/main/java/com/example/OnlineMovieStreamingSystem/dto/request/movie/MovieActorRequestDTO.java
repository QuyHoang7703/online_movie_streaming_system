package com.example.OnlineMovieStreamingSystem.dto.request.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieActorRequestDTO {
    private long actorId;
    private String characterName;
}
