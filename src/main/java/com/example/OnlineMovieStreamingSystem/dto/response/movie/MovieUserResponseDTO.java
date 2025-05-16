package com.example.OnlineMovieStreamingSystem.dto.response.movie;

import com.example.OnlineMovieStreamingSystem.dto.response.genre.GenreSummaryDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.videoVersion.VideoVersionDetailResponseDTO;
import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import com.example.OnlineMovieStreamingSystem.util.constant.VideoType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieUserResponseDTO {
    private long movieId;
    private MovieType movieType;
    private String title;
    private String originalTitle;
    private String posterUrl;
    private String backdropUrl;
    private double voteAverage;
    private int year;
    private List<GenreSummaryDTO> genres;
    private List<VideoVersionDetailResponseDTO> videoVersions;

    private int duration;

    private int season;
    private int totalEpisodes;


}
