package com.example.OnlineMovieStreamingSystem.dto.request.user;

import com.example.OnlineMovieStreamingSystem.util.constant.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDTO {
    private String name;
    private String phoneNumber;
    private String address;
    private String avatarUrl;
    private GenderEnum gender;

}
