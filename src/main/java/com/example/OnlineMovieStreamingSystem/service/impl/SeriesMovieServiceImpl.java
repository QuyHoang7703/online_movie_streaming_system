package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.domain.SeriesMovie;
import com.example.OnlineMovieStreamingSystem.domain.StandaloneMovie;
import com.example.OnlineMovieStreamingSystem.dto.request.movie.SeriesMovieRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.SeriesMovieResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.StandaloneMovieResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.MovieRepository;
import com.example.OnlineMovieStreamingSystem.service.MovieService;
import com.example.OnlineMovieStreamingSystem.service.SeriesMovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SeriesMovieServiceImpl implements SeriesMovieService {
    private final MovieRepository movieRepository;
    private final MovieService movieService;
    @Override
    public SeriesMovieResponseDTO createSeriesMovie(SeriesMovieRequestDTO seriesMovieRequestDTO, MultipartFile poster, MultipartFile backdrop) throws IOException {
        Movie movie = this.movieService.createMovieFromDTO(seriesMovieRequestDTO, poster, backdrop);

        //  Set attributes for series movie
        SeriesMovie seriesMovie = new SeriesMovie();
        seriesMovie.setSeason(seriesMovieRequestDTO.getSeason());
        seriesMovie.setEpisodeNumber(seriesMovieRequestDTO.getEpisodeNumber());
        seriesMovie.setMovie(movie);
        movie.setSeriesMovie(seriesMovie);

        Movie savedMovie = this.movieRepository.save(movie);

        return this.convertToSeriesResponseDTO(savedMovie);
    }

    private SeriesMovieResponseDTO convertToSeriesResponseDTO(Movie movie){
        SeriesMovieResponseDTO seriesMovieResponseDTO = this.movieService.convertToMovieInfoDTO(movie, SeriesMovieResponseDTO.class);
        seriesMovieResponseDTO.setSeason(movie.getSeriesMovie().getSeason());
        seriesMovieResponseDTO.setEpisodeNumber(movie.getSeriesMovie().getEpisodeNumber());
        return seriesMovieResponseDTO;
    }
}
