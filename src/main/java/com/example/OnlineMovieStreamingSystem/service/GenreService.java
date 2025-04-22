package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.genre.GenreRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.genre.GenreResponseDTO;

import java.util.List;

public interface GenreService {
    GenreResponseDTO createGenre(GenreRequestDTO genreRequestDTO);
    GenreResponseDTO updateGenre(long id, GenreRequestDTO genreRequestDTO);
    ResultPaginationDTO getAllGenres(String genre, int pageNumber, int pageSize);
    void deleteGenre(long id);
    GenreResponseDTO getGenreById(long id);
}
