package com.example.OnlineMovieStreamingSystem.dto.response.genre;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenreSummaryDTO {
    private long id;
    private String name;
}
