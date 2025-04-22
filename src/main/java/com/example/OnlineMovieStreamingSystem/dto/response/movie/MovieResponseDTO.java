package com.example.OnlineMovieStreamingSystem.dto.response.movie;
import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDate;

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
    private Instant createAt;
    private Instant updateAt;
}
