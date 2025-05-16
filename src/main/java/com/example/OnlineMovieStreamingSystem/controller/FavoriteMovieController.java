package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.service.FavoriteMovieService;
import com.example.OnlineMovieStreamingSystem.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/favorite-movies")
@RequiredArgsConstructor
public class FavoriteMovieController {
    private final FavoriteMovieService favoriteMovieService;

    @PostMapping
    @ApiMessage("Đã thêm vào danh sách yêu thích")
    public ResponseEntity<Void> addFavoriteMovie(@RequestParam long movieId) {
        this.favoriteMovieService.addFavoriteMovie(movieId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @DeleteMapping("{movieId}")
    @ApiMessage("Đã xóa khỏi danh sách yêu thích")
    public ResponseEntity<Void> deleteFavoriteMovie(@PathVariable("movieId") long movieId) {
        this.favoriteMovieService.removeFavoriteMovie(movieId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getFavoriteMoviesForUser(@RequestParam(name = "page", defaultValue = "1") int page,
                                                                        @RequestParam(name = "size", defaultValue = "6") int size) {

        return ResponseEntity.status(HttpStatus.OK).body(this.favoriteMovieService.getFavoriteMovies(page, size));
    }
}
