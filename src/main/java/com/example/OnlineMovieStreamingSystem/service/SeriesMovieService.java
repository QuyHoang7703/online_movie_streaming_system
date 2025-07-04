package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.dto.request.movie.SeriesMovieRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.SeriesMovieResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SeriesMovieService {
    SeriesMovieResponseDTO createSeriesMovie(SeriesMovieRequestDTO seriesMovieRequestDTO, MultipartFile poster, MultipartFile backdrop) throws IOException;
    SeriesMovieResponseDTO updateStandaloneMovie(long movieId, SeriesMovieRequestDTO seriesMovieRequestDTO, MultipartFile poster, MultipartFile backdrop) throws IOException;
    SeriesMovieResponseDTO getSeriesMovie(long movieId);
    SeriesMovieResponseDTO convertToSeriesResponseDTO(Movie movie);
}
