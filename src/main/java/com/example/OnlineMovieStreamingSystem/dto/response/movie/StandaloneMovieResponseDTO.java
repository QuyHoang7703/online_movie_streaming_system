package com.example.OnlineMovieStreamingSystem.dto.response.movie;

import com.example.OnlineMovieStreamingSystem.dto.response.videoVersion.VideoVersionResponseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class StandaloneMovieResponseDTO extends MovieResponseDTO {
    private double budget;
    private double revenue;
//    private List<VideoVersionResponseDTO> videoVersions;
}
