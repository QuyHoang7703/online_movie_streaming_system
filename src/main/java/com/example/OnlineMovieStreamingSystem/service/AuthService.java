package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.request.LoginRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO loginAuthToken(LoginRequestDTO loginRequestDTO);
    AuthResponseDTO refreshAuthToken(String refreshToken);
    void logoutAuthToken();


}
