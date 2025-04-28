package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.request.movie.SeriesMovieRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.SeriesMovieResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.SeriesMovieService;
import com.example.OnlineMovieStreamingSystem.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/series-movie")
public class SeriesMovieController {
    private final SeriesMovieService seriesMovieService;

    @PostMapping
    @ApiMessage("Thêm phim thành công")
    public ResponseEntity<SeriesMovieResponseDTO> createStandaloneMovie(@RequestPart(name="movieInfo") SeriesMovieRequestDTO seriesMovieRequestDTO,
                                                                            @RequestParam("poster") MultipartFile poster,
                                                                            @RequestParam("backdrop") MultipartFile backdrop) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.seriesMovieService.createSeriesMovie(seriesMovieRequestDTO, poster, backdrop));
    }

    @PatchMapping("{movieId}")
    @ApiMessage("Cập nhập phim thành công")
    public ResponseEntity<SeriesMovieResponseDTO> updateStandaloneMovie(@PathVariable long movieId,
                                                                        @RequestPart(name="movieInfo") SeriesMovieRequestDTO seriesMovieRequestDTO,
                                                                        @RequestParam(name="poster", required = false)MultipartFile poster,
                                                                        @RequestParam(name="backdrop", required = false) MultipartFile backdrop) throws IOException {

        return ResponseEntity.status(HttpStatus.OK).body(this.seriesMovieService.updateStandaloneMovie(movieId, seriesMovieRequestDTO, poster, backdrop));
    }

    @GetMapping("{movieId}")
    public ResponseEntity<SeriesMovieResponseDTO> getStandaloneMovie(@PathVariable long movieId) {

        return ResponseEntity.status(HttpStatus.OK).body(this.seriesMovieService.getSeriesMovie(movieId));
    }
}
