package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.domain.SeriesMovie;
import com.example.OnlineMovieStreamingSystem.domain.StandaloneMovie;
import com.example.OnlineMovieStreamingSystem.dto.request.movie.SeriesMovieRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.SeriesMovieResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.MovieRepository;
import com.example.OnlineMovieStreamingSystem.service.MovieRedisService;
import com.example.OnlineMovieStreamingSystem.service.MovieService;
import com.example.OnlineMovieStreamingSystem.service.SeriesMovieService;
import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SeriesMovieServiceImpl implements SeriesMovieService {
    private final MovieRepository movieRepository;
    private final MovieService movieService;
    private final MovieRedisService movieRedisService;

    @Transactional
    @Override
    public SeriesMovieResponseDTO createSeriesMovie(SeriesMovieRequestDTO seriesMovieRequestDTO, MultipartFile poster, MultipartFile backdrop) throws IOException {
        Movie movie = this.movieService.createMovieFromDTO(seriesMovieRequestDTO, poster, backdrop);

        //  Set attributes for series movie
        SeriesMovie seriesMovie = new SeriesMovie();
        seriesMovie.setSeason(seriesMovieRequestDTO.getSeason());
        seriesMovie.setTotalEpisodes(seriesMovieRequestDTO.getTotalEpisodes());
        seriesMovie.setMovie(movie);
        movie.setSeriesMovie(seriesMovie);

        Movie savedMovie = this.movieRepository.save(movie);
        this.movieRedisService.clearMovieInRedis();

        return this.convertToSeriesResponseDTO(savedMovie);
    }

    @Transactional
    @Override
    public SeriesMovieResponseDTO updateStandaloneMovie(long movieId, SeriesMovieRequestDTO seriesMovieRequestDTO, MultipartFile poster, MultipartFile backdrop) throws IOException {
        Movie updateMovie = this.movieService.updateMovieFromDTO(movieId, seriesMovieRequestDTO, poster, backdrop);
        SeriesMovie seriesMovie = updateMovie.getSeriesMovie();

        Integer newSeason = seriesMovieRequestDTO.getSeason();
        if(newSeason != null && !Objects.equals(newSeason, seriesMovie.getSeason())) {
            seriesMovie.setSeason(seriesMovieRequestDTO.getSeason());
        }

        Integer newTotalEpisodes = seriesMovieRequestDTO.getTotalEpisodes();
        if(newTotalEpisodes != null && !Objects.equals(newTotalEpisodes, seriesMovie.getTotalEpisodes())) {
            seriesMovie.setTotalEpisodes(seriesMovieRequestDTO.getTotalEpisodes());
        }
        updateMovie.setSeriesMovie(seriesMovie);
        Movie updatedMovie = this.movieRepository.save(updateMovie);
        this.movieRedisService.clearMovieInRedis();

        return this.convertToSeriesResponseDTO(updatedMovie);
    }

    @Override
    public SeriesMovieResponseDTO getSeriesMovie(long movieId) {
        Movie movie = this.movieRepository.findById(movieId)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy phim"));
        if(movie.getMovieType() != MovieType.SERIES){
            throw new ApplicationException("Phim này không phải là phim bộ");
        }
        SeriesMovieResponseDTO seriesMovieResponseDTO = this.convertToSeriesResponseDTO(movie);

        return seriesMovieResponseDTO;
    }

    @Override
    public SeriesMovieResponseDTO convertToSeriesResponseDTO(Movie movie){
        SeriesMovieResponseDTO seriesMovieResponseDTO = this.movieService.convertToMovieInfoDTO(movie, SeriesMovieResponseDTO.class);
        if(movie.getSeriesMovie() != null) {
            seriesMovieResponseDTO.setSeason(movie.getSeriesMovie().getSeason());
            seriesMovieResponseDTO.setTotalEpisodes(movie.getSeriesMovie().getTotalEpisodes());
        }

        return seriesMovieResponseDTO;
    }
}
