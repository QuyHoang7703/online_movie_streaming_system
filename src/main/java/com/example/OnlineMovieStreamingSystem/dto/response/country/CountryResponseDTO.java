package com.example.OnlineMovieStreamingSystem.dto.response.country;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CountryResponseDTO {
    private String id;
    private String name;
}
