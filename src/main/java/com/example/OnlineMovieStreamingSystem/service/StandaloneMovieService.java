package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.request.movie.StandaloneMovieRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.StandaloneMovieResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StandaloneMovieService {
    StandaloneMovieResponseDTO createStandaloneMovie(StandaloneMovieRequestDTO standaloneMovieRequestDTO, MultipartFile poster, MultipartFile backdrop) throws IOException;

}
