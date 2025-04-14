package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.response.AuthResponseDTO;

public interface UserService {
    void checkActiveUser(String email);
    AuthResponseDTO convertToLoginResponseDTO(String email);
}
