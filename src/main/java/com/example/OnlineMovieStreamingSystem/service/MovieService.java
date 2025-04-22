package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.dto.request.movie.MovieRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MovieService {
    Movie createMovieFromDTO(MovieRequestDTO movieRequestDTO, MultipartFile poster, MultipartFile backdrop) throws IOException;
    <T extends MovieResponseDTO> T convertToMovieInfoDTO(Movie movie, Class<T> clazz);
}
