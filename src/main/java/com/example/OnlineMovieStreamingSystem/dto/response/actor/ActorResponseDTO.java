package com.example.OnlineMovieStreamingSystem.dto.response.actor;

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
    private String placeOfBirth;
    private String avatarUrl;
}
