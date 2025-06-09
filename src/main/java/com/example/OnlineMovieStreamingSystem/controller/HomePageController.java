package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieUserResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.MovieUserService;
import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/home-page")
public class HomePageController {
    private final MovieUserService movieUserService;

    @GetMapping("/hot-movies")
    public ResponseEntity<ResultPaginationDTO> getHotMovieByFilter(@RequestParam(name = "size", defaultValue = "10") int size,
                                                                   @RequestParam(name = "movieType", required = false) MovieType movieType,
                                                                   @RequestParam(name = "country", required = false) String countryId) {

        return ResponseEntity.status(HttpStatus.OK).body(this.movieUserService.getHotMovieByMovieType(movieType, countryId, size));
    }
}
