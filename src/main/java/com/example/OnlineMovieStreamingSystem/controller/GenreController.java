package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.genre.GenreRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.genre.GenreResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.GenreService;
import com.example.OnlineMovieStreamingSystem.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/genres")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class GenreController {
    private final GenreService genreService;

    @PostMapping
    @ApiMessage("Created new genre")
    public ResponseEntity<GenreResponseDTO> createGenre(@RequestBody GenreRequestDTO genreRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.genreService.createGenre(genreRequestDTO));
    }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getGenreByName(@RequestParam(name="genreName", required = false) String genreName,
                                                              @RequestParam(name="page", defaultValue = "1") int page,
                                                              @RequestParam(name="size", defaultValue = "5") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(this.genreService.getAllGenres(genreName, page, size));
    }

    @DeleteMapping("{genreId}")
    @ApiMessage("Deleted the genre")
    public ResponseEntity<Void> deleteGenre(@PathVariable("genreId") Long genreId) {
        this.genreService.deleteGenre(genreId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PatchMapping("{genreId}")
    @ApiMessage("Updated the genre")
    public ResponseEntity<GenreResponseDTO> updateGenre(@PathVariable("genreId") long genreId,
                                                        @RequestBody GenreRequestDTO genreRequestDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(this.genreService.updateGenre(genreId, genreRequestDTO));
    }

    @GetMapping("{genreId}")
    public ResponseEntity<GenreResponseDTO> getGenreById(@PathVariable("genreId") Long genreId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.genreService.getGenreById(genreId));
    }
}
