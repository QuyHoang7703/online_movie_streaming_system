package com.example.OnlineMovieStreamingSystem.dto.request.movie;

import com.example.OnlineMovieStreamingSystem.dto.request.videoVersion.VideoUrlRequestDTO;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StandaloneMovieRequestDTO extends MovieRequestDTO {
    private double budget;
    private double revenue;

}
