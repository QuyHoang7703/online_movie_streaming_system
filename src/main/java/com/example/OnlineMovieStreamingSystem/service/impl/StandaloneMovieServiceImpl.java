package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.domain.StandaloneMovie;
import com.example.OnlineMovieStreamingSystem.domain.VideoVersion;
import com.example.OnlineMovieStreamingSystem.dto.request.movie.StandaloneMovieRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.videoVersion.VideoUrlRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.StandaloneMovieResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.videoVersion.VideoVersionResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.MovieRepository;
import com.example.OnlineMovieStreamingSystem.service.*;
import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import com.example.OnlineMovieStreamingSystem.util.constant.VideoType;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StandaloneMovieServiceImpl implements StandaloneMovieService {
    private final MovieRepository movieRepository;
    private final MovieService movieService;
    private final String CONTAINER_NAME = "movie-video-container";
    private final MovieRedisService movieRedisService;
    @Transactional
    @Override
    public StandaloneMovieResponseDTO createStandaloneMovie(StandaloneMovieRequestDTO standaloneMovieRequestDTO,
                                                            MultipartFile poster,
                                                            MultipartFile backdrop) throws IOException {

        Movie movie = this.movieService.createMovieFromDTO(standaloneMovieRequestDTO, poster, backdrop);

        //  Set attributes for standalone movie
        StandaloneMovie standaloneMovie = new StandaloneMovie();
        standaloneMovie.setRevenue(standaloneMovieRequestDTO.getRevenue());
        standaloneMovie.setBudget(standaloneMovieRequestDTO.getBudget());
//        standaloneMovie.setDuration(standaloneMovieRequestDTO.getDuration());

        standaloneMovie.setMovie(movie);
        movie.setStandaloneMovie(standaloneMovie);


        Movie savedMovie = this.movieRepository.save(movie);
        this.movieRedisService.clearMovieInRedis();
        return this.convertToStandaloneMovieResponseDTO(savedMovie);
    }

    @Transactional
    @Override
    public StandaloneMovieResponseDTO updateStandaloneMovie(long movieId,
                                                            StandaloneMovieRequestDTO standaloneMovieRequestDTO,
                                                            MultipartFile poster,
                                                            MultipartFile backdrop,
                                                            MultipartFile video) throws IOException {
        Movie updateMovie = this.movieService.updateMovieFromDTO(movieId, standaloneMovieRequestDTO, poster, backdrop);
        StandaloneMovie standaloneMovie = updateMovie.getStandaloneMovie();

        if(!Objects.equals(standaloneMovie.getBudget(), standaloneMovieRequestDTO.getBudget())) {
           standaloneMovie.setBudget(standaloneMovieRequestDTO.getBudget());
        }

        if(!Objects.equals(standaloneMovie.getRevenue(), standaloneMovieRequestDTO.getRevenue())) {
           standaloneMovie.setRevenue(standaloneMovieRequestDTO.getRevenue());
        }

        updateMovie.setStandaloneMovie(standaloneMovie);
        Movie updatedMovie = this.movieRepository.save(updateMovie);
        this.movieRedisService.clearMovieInRedis();

        return this.convertToStandaloneMovieResponseDTO(updatedMovie);
    }

    @Override
    public StandaloneMovieResponseDTO getStandaloneMovie(long movieId) {
        Movie movie = this.movieRepository.findById(movieId)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy phim"));
        if(movie.getMovieType() != MovieType.STANDALONE){
            throw new ApplicationException("Phim này không phải là phim lẻ");
        }
        StandaloneMovieResponseDTO standaloneMovieResponseDTO = this.convertToStandaloneMovieResponseDTO(movie);

        return standaloneMovieResponseDTO;
    }

    @Override
    public StandaloneMovieResponseDTO convertToStandaloneMovieResponseDTO(Movie movie){
        StandaloneMovieResponseDTO standaloneMovieResponseDTO = this.movieService.convertToMovieInfoDTO(movie, StandaloneMovieResponseDTO.class);
        standaloneMovieResponseDTO.setBudget(movie.getStandaloneMovie().getBudget());
        standaloneMovieResponseDTO.setRevenue(movie.getStandaloneMovie().getRevenue());


        return standaloneMovieResponseDTO;
    }


}
