package com.example.OnlineMovieStreamingSystem.dto;

import com.example.OnlineMovieStreamingSystem.util.constant.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {
    private long id;
    private String email;
    private String name;
    private String avatarUrl;
    private String role;
    private String phoneNumber;
    private String address;
    private GenderEnum gender;

}
