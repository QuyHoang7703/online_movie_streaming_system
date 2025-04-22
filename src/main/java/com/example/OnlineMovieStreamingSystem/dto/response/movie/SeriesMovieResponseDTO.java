package com.example.OnlineMovieStreamingSystem.dto.response.movie;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SeriesMovieResponseDTO extends MovieResponseDTO {
    private int season;
    private int episodeNumber;
}
