package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.request.movie.StandaloneMovieRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.StandaloneMovieResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.StandaloneMovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/standalone-movies")
public class StandaloneMovieController {
    private final StandaloneMovieService standaloneMovieService;

    @PostMapping
    public ResponseEntity<StandaloneMovieResponseDTO> createStandaloneMovie(@RequestPart(name="movieInfo") StandaloneMovieRequestDTO standaloneMovieRequestDTO,
                                                                            @RequestParam("poster")MultipartFile poster,
                                                                            @RequestParam("backdrop") MultipartFile backdrop) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.standaloneMovieService.createStandaloneMovie(standaloneMovieRequestDTO, poster, backdrop));
    }

    @PatchMapping("{movieId}")
    public ResponseEntity<StandaloneMovieResponseDTO> updateStandaloneMovie(@PathVariable long movieId,
                                                                            @RequestPart(name="movieInfo") StandaloneMovieRequestDTO standaloneMovieRequestDTO,
                                                                            @RequestParam(name="poster", required = false)MultipartFile poster,
                                                                            @RequestParam(name="backdrop", required = false) MultipartFile backdrop) throws IOException {

        return ResponseEntity.status(HttpStatus.OK).body(this.standaloneMovieService.updateStandaloneMovie(movieId, standaloneMovieRequestDTO, poster, backdrop));
    }

    @GetMapping("{movieId}")
    public ResponseEntity<StandaloneMovieResponseDTO> getStandaloneMovie(@PathVariable long movieId) {

        return ResponseEntity.status(HttpStatus.OK).body(this.standaloneMovieService.getStandaloneMovie(movieId));
    }







}
