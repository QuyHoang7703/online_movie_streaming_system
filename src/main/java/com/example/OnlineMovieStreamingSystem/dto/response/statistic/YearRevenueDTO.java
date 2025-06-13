package com.example.OnlineMovieStreamingSystem.dto.response.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class YearRevenueDTO {
    private Integer year;
    private Double revenue;
}
