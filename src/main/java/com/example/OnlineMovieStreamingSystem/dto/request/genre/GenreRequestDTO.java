package com.example.OnlineMovieStreamingSystem.dto.request.genre;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenreRequestDTO {
    @NotBlank(message = "Name genre cannot be left blank")
    private String name;
    private String description;
}
