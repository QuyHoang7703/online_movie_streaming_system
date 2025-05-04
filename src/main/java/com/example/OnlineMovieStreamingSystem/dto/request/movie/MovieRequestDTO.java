package com.example.OnlineMovieStreamingSystem.dto.request.movie;

import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieRequestDTO {
    private String title;
    private String description;
    private String director;
    private String country;
    private LocalDate releaseDate;
    private boolean free;
    private String trailerUrl;
    private MovieType movieType;
    private List<Long> genreIds;
    private List<MovieActorRequestDTO> movieActors;
    private List<Long> subscriptionPlanIds;


}
