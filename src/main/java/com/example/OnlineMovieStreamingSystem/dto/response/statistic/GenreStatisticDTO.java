package com.example.OnlineMovieStreamingSystem.dto.response.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GenreStatisticDTO {
    private String genreName;
    private long count;
}
