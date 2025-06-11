package com.example.OnlineMovieStreamingSystem.dto.response.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OverviewStatisticResponseDTO {
    private long totalUsers;
    private long standaloneMovies;
    private long seriesMovies;
    private double currentMonthRevenue;
}
