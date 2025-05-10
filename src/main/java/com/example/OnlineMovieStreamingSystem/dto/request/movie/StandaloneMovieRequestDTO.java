package com.example.OnlineMovieStreamingSystem.dto.request.movie;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StandaloneMovieRequestDTO extends MovieRequestDTO {
    private Integer duration;
    private String videoUrl;
}
