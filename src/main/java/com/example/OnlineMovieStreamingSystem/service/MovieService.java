package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.movie.MovieRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieUserResponseDTO;
import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MovieService {
    Movie createMovieFromDTO(MovieRequestDTO movieRequestDTO, MultipartFile poster, MultipartFile backdrop) throws IOException;
    <T extends MovieResponseDTO> T convertToMovieInfoDTO(Movie movie, Class<T> clazz);
    ResultPaginationDTO getMoviesForAdmin(String title,
                                  List<String> genreNames,
                                  String movieType,
                                  List<String> countries,
                                  int page,
                                  int size) throws BadRequestException;
    List<String> getAllCountriesOfMovie();
    Movie updateMovieFromDTO(long movieId, MovieRequestDTO movieRequestDTO, MultipartFile poster, MultipartFile backdrop) throws IOException;
    void deleteMovie(long movieId);
    MovieUserResponseDTO convertToMovieUserResponseDTO(Movie movie);
    ResultPaginationDTO getMoviesForUser(String title,
                                         List<String> genreNames,
                                         String movieType,
                                         List<String> countries,
                                         int page,
                                         int size) throws BadRequestException;


}
