package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.request.userInteraction.UserInteractionRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.userInteraction.UserInteractionResponseDTO;

public interface UserInteractionService {
    UserInteractionResponseDTO addUserInteraction(UserInteractionRequestDTO userInteractionRequestDTO);
    UserInteractionResponseDTO updateUserInteraction(UserInteractionRequestDTO userInteractionRequestDTO);
    UserInteractionResponseDTO getUserInteraction(long movieId);
}
