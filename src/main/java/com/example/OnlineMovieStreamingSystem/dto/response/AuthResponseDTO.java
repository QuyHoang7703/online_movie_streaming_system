package com.example.OnlineMovieStreamingSystem.dto.response;

import com.example.OnlineMovieStreamingSystem.dto.UserInfoDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {
    private UserInfoDTO userInfo;
    private String accessToken;
    @JsonIgnore
    private String refreshToken;
}
