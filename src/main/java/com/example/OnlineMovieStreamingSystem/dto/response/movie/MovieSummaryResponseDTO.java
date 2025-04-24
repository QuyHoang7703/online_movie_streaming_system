package com.example.OnlineMovieStreamingSystem.dto.response.movie;

import com.example.OnlineMovieStreamingSystem.dto.response.genre.GenreSummaryDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.SubscriptionPlanSummaryDTO;
import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MovieSummaryResponseDTO {
    private long id;
    private String posterUrl;
    private String title;
    private String director;
    private List<GenreSummaryDTO> genres;
    private List<SubscriptionPlanSummaryDTO> subscriptionPlans;
    private LocalDate releaseDate;
    private MovieType movieType;


}
