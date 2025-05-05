package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.response.AuthResponseDTO;

public interface Oauth2Service {
    AuthResponseDTO handleLoginWithClient(String code);
}
