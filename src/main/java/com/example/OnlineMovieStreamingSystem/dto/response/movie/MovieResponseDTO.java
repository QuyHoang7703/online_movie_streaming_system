package com.example.OnlineMovieStreamingSystem.dto.response.movie;
import com.example.OnlineMovieStreamingSystem.dto.response.genre.GenreSummaryDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.SubscriptionPlanSummaryDTO;
import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MovieResponseDTO {
    private long id;
    private String title;
    private String description;
    private String director;
    private String posterUrl;
    private String backdropUrl;
    private String country;
    private LocalDate releaseDate;
    private boolean isFree;
    private String trailerUrl;
    private MovieType movieType;
    private List<MovieActorResponseDTO> movieActors;
    private List<GenreSummaryDTO> genres;
    private List<SubscriptionPlanSummaryDTO> subscriptionPlans;
    private Instant createAt;
    private Instant updateAt;
}
