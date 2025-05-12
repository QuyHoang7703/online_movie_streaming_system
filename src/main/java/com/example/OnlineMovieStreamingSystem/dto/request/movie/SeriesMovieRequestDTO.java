package com.example.OnlineMovieStreamingSystem.dto.request.movie;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SeriesMovieRequestDTO extends MovieRequestDTO {
    private int season;
    private int totalEpisodes;
}
