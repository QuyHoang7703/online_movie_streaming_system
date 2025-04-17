package com.example.OnlineMovieStreamingSystem.dto.response.genre;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenreResponseDTO {
    private int id;
    private String name;
    private String description;

}
