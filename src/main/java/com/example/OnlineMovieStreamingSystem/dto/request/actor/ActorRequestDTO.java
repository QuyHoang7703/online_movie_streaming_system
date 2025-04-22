package com.example.OnlineMovieStreamingSystem.dto.request.actor;

import com.example.OnlineMovieStreamingSystem.util.constant.GenderEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActorRequestDTO {
    @NotBlank(message = "Actor name cannot left blank")
    private String name;
    private LocalDate birthDate;
    private String biography;
    private String otherName;
    private String placeOfBirth;
    private GenderEnum gender;

}
