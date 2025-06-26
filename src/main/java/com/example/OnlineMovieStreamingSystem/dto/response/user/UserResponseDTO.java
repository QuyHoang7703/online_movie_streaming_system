package com.example.OnlineMovieStreamingSystem.dto.response.user;

import com.example.OnlineMovieStreamingSystem.util.constant.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {
    private long id;
    private String email;
    private String name;
    private String phoneNumber;
    private String address;
    private String avatarUrl;
    private String role;
    private GenderEnum gender;
}
