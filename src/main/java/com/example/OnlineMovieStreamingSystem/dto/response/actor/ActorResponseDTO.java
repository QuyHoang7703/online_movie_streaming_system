package com.example.OnlineMovieStreamingSystem.dto.response.actor;

import com.example.OnlineMovieStreamingSystem.util.constant.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActorResponseDTO {
    private long id;
    private String name;
    private LocalDate birthDate;
    private String avatarUrl;
    private String biography;
    private String otherName;
    private GenderEnum gender;

    // Có thêm movie dto ở đây

}
