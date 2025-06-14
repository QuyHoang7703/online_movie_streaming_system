package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.dto.request.movie.StandaloneMovieRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.StandaloneMovieResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StandaloneMovieService {
    StandaloneMovieResponseDTO createStandaloneMovie(StandaloneMovieRequestDTO standaloneMovieRequestDTO,
                                                     MultipartFile poster,
                                                     MultipartFile backdrop) throws IOException;
    StandaloneMovieResponseDTO updateStandaloneMovie(long movieId, StandaloneMovieRequestDTO standaloneMovieRequestDTO, MultipartFile poster, MultipartFile backdrop, MultipartFile video) throws IOException;
    StandaloneMovieResponseDTO getStandaloneMovie(long movieId);
    StandaloneMovieResponseDTO convertToStandaloneMovieResponseDTO(Movie movie);
}
