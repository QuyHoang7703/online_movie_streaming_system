package com.example.OnlineMovieStreamingSystem.dto.request.movie;

import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private String originalTitle;
    private String description;
    private String director;
//    private String country;
    private LocalDate releaseDate;
    private Boolean free;
    private String trailerUrl;
    @Enumerated(EnumType.STRING)
    private MovieType movieType;
    private String status;
    private double voteAverage;
    private double voteCount;
    private String quality;
    private List<Long> genreIds;
    private List<MovieActorRequestDTO> movieActors;
    private List<Long> subscriptionPlanIds;
    private List<String> countryIds;


}
