package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.service.MovieService;
import com.example.OnlineMovieStreamingSystem.util.annotation.ApiMessage;
import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getMovies(@RequestParam(name="title", required = false) String title,
                                                         @RequestParam(name="genreNames", required = false) List<String> genreNames,
                                                         @RequestParam(name="movieType", required = false) String movieType,
                                                         @RequestParam(name="countries", required = false) List<String> countries,
                                                         @RequestParam(name="page", defaultValue = "1") int page,
                                                         @RequestParam(name="size", defaultValue = "10") int size) throws BadRequestException {

        return ResponseEntity.status(HttpStatus.OK).body(this.movieService.getMovies(title, genreNames, movieType, countries, page, size));
    }


    @GetMapping("/countries")
    public ResponseEntity<List<String>> getCountriesOfMovies() {
        return ResponseEntity.status(HttpStatus.OK).body(this.movieService.getAllCountriesOfMovie());
    }

    @DeleteMapping("{movieId}")
    @ApiMessage("Đã xóa phim thành công")
    public ResponseEntity<Void> deleteMovie(@PathVariable("movieId") int movieId) {
        this.movieService.deleteMovie(movieId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }



}
