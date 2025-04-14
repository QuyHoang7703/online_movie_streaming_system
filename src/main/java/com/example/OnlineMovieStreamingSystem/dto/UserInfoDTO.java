package com.example.OnlineMovieStreamingSystem.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoDTO {
    private String email;
    private String name;
    private String avatarUrl;
    private String role;

}
