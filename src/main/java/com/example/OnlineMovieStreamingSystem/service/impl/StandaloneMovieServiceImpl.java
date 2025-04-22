package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.domain.StandaloneMovie;
import com.example.OnlineMovieStreamingSystem.dto.request.movie.StandaloneMovieRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.StandaloneMovieResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.MovieRepository;
import com.example.OnlineMovieStreamingSystem.service.MovieService;
import com.example.OnlineMovieStreamingSystem.service.StandaloneMovieService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class StandaloneMovieServiceImpl implements StandaloneMovieService {
    private final MovieRepository movieRepository;
    private final MovieService movieService;

    @Override
    @Transactional
    public StandaloneMovieResponseDTO createStandaloneMovie(StandaloneMovieRequestDTO standaloneMovieRequestDTO, MultipartFile poster, MultipartFile backdrop) throws IOException {

        Movie movie = this.movieService.createMovieFromDTO(standaloneMovieRequestDTO, poster, backdrop);

        //  Set attributes for standalone movie
        StandaloneMovie standaloneMovie = new StandaloneMovie();
        standaloneMovie.setDuration(standaloneMovieRequestDTO.getDuration());
        standaloneMovie.setVideoUrl(standaloneMovieRequestDTO.getVideoUrl());
        standaloneMovie.setMovie(movie);
        movie.setStandaloneMovie(standaloneMovie);

        Movie savedMovie = this.movieRepository.save(movie);

        return this.convertToStandaloneMovieResponseDTO(savedMovie);
    }

    private StandaloneMovieResponseDTO convertToStandaloneMovieResponseDTO(Movie movie){
        StandaloneMovieResponseDTO standaloneMovieResponseDTO = this.movieService.convertToMovieInfoDTO(movie, StandaloneMovieResponseDTO.class);
        standaloneMovieResponseDTO.setDuration(movie.getStandaloneMovie().getDuration());
        standaloneMovieResponseDTO.setVideoUrl(movie.getStandaloneMovie().getVideoUrl());
        return standaloneMovieResponseDTO;
    }
}
