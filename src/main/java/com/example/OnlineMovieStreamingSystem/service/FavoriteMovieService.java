package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;

public interface FavoriteMovieService {
    void addFavoriteMovie(long movieId);
    void removeFavoriteMovie(long movieId);
    ResultPaginationDTO getFavoriteMovies(int page, int size);
}
