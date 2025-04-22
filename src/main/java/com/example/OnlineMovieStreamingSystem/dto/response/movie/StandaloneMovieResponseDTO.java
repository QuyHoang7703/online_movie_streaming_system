package com.example.OnlineMovieStreamingSystem.dto.response.movie;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class StandaloneMovieResponseDTO extends MovieResponseDTO {
    private int duration;
    private String videoUrl;
}
