package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.domain.StandaloneMovie;
import com.example.OnlineMovieStreamingSystem.dto.request.movie.StandaloneMovieRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.StandaloneMovieResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.MovieRepository;
import com.example.OnlineMovieStreamingSystem.service.MovieService;
import com.example.OnlineMovieStreamingSystem.service.StandaloneMovieService;
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
public class StandaloneMovieServiceImpl implements StandaloneMovieService {
    private final MovieRepository movieRepository;
    private final MovieService movieService;

    @Transactional
    @Override

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

    @Transactional
    @Override
    public StandaloneMovieResponseDTO updateStandaloneMovie(long movieId,
                                                            StandaloneMovieRequestDTO standaloneMovieRequestDTO,
                                                            MultipartFile poster,
                                                            MultipartFile backdrop) throws IOException {
        Movie updateMovie = this.movieService.updateMovieFromDTO(movieId, standaloneMovieRequestDTO, poster, backdrop);
        StandaloneMovie standaloneMovie = updateMovie.getStandaloneMovie();
        if(!Objects.equals(standaloneMovie.getDuration(), standaloneMovieRequestDTO.getDuration())) {
           standaloneMovie.setDuration(standaloneMovieRequestDTO.getDuration());
        }
        if(!Objects.equals(standaloneMovie.getVideoUrl(), standaloneMovieRequestDTO.getVideoUrl())) {
            standaloneMovie.setVideoUrl(standaloneMovieRequestDTO.getVideoUrl());
        }
        updateMovie.setStandaloneMovie(standaloneMovie);
        Movie updatedMovie = this.movieRepository.save(updateMovie);



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

    private StandaloneMovieResponseDTO convertToStandaloneMovieResponseDTO(Movie movie){
        StandaloneMovieResponseDTO standaloneMovieResponseDTO = this.movieService.convertToMovieInfoDTO(movie, StandaloneMovieResponseDTO.class);
        standaloneMovieResponseDTO.setDuration(movie.getStandaloneMovie().getDuration());
        standaloneMovieResponseDTO.setVideoUrl(movie.getStandaloneMovie().getVideoUrl());
        return standaloneMovieResponseDTO;
    }
}
