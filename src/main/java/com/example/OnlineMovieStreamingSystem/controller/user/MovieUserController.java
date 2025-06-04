package com.example.OnlineMovieStreamingSystem.controller.user;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.recommendMovie.RecommendationMovieRequest;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieUserResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.SubscriptionPlanResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.MovieService;
import com.example.OnlineMovieStreamingSystem.service.MovieUserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user/movies")
@RequiredArgsConstructor
public class MovieUserController {
    private final MovieService movieService;
    private final MovieUserService movieUserService;

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getMoviesForUser (@RequestParam(name="title", required = false) String title,
                                                                 @RequestParam(name="genreNames", required = false) List<String> genreNames,
                                                                 @RequestParam(name="movieType", required = false) String movieType,
                                                                 @RequestParam(name="countries", required = false) List<String> countries,
                                                                 @RequestParam(name="page", defaultValue = "1") int page,
                                                                 @RequestParam(name="size", defaultValue = "10") int size) throws BadRequestException {

        return ResponseEntity.status(HttpStatus.OK).body(this.movieService.getMoviesForUser(title, genreNames, movieType, countries, page, size));
    }

    @GetMapping("/{movieId}/can-watch")
    public ResponseEntity<Boolean> canUserWatchMovie(@PathVariable("movieId") long movieId) {

        return ResponseEntity.status(HttpStatus.OK).body(this.movieUserService.canUserWatchMovie(movieId));
    }

    @GetMapping("{movieId}/subscription-plans")
    public ResponseEntity<List<SubscriptionPlanResponseDTO>> getSubscriptionPlansForMovie(@PathVariable("movieId") long movieId) {

        return ResponseEntity.status(HttpStatus.OK).body(this.movieUserService.getSubscriptionPlansForMovie(movieId));
    }

    @PostMapping("/recommend")
    public ResponseEntity<List<MovieUserResponseDTO>> getRecommendMovies(@RequestBody RecommendationMovieRequest recommendationMovieRequest) {

        return ResponseEntity.status(HttpStatus.OK).body(this.movieUserService.getRecommendationsForMovie(recommendationMovieRequest));
    }





}
