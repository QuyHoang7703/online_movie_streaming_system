package com.example.OnlineMovieStreamingSystem.dto.response;

import com.example.OnlineMovieStreamingSystem.dto.UserInfoDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {
    private UserInfoDTO userInfo;
    private String accessToken;
    @JsonIgnore
    private String refreshToken;
}
