package com.example.OnlineMovieStreamingSystem.controller;

import com.azure.core.annotation.Get;
import com.example.OnlineMovieStreamingSystem.dto.response.statistic.GenreStatisticDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.statistic.MonthlyRevenueResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.statistic.OverviewStatisticResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.statistic.YearRevenueDTO;
import com.example.OnlineMovieStreamingSystem.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/statistics")
public class StatisticController {
    private final StatisticService statisticService;
    @GetMapping("/overview")
    public ResponseEntity<OverviewStatisticResponseDTO> getOverviewStatistic(@RequestParam("month") int month,
                                                                             @RequestParam("year") int year) {
        return ResponseEntity.status(HttpStatus.OK).body(this.statisticService.getOverviewStatistic(month, year));
    }

    @GetMapping("/monthly-revenue")
    public ResponseEntity<List<MonthlyRevenueResponseDTO>> getMonthlyRevenue(@RequestParam("year") int year) {
        return ResponseEntity.status(HttpStatus.OK).body(this.statisticService.getMonthlyRevenue(year));
    }

    @GetMapping("/movies-by-genre")
    public ResponseEntity<List<GenreStatisticDTO>> getGenreStatistic() {
        return ResponseEntity.status(HttpStatus.OK).body(this.statisticService.getGenreStatistic());
    }

    @GetMapping("/yearly-revenue")
    public ResponseEntity<List<YearRevenueDTO>> getYearlyRevenue(@RequestParam("startYear") int startYear,
                                                                 @RequestParam("endYear") int endYear) {
        return ResponseEntity.status(HttpStatus.OK).body(this.statisticService.getYearRevenue(startYear, endYear));
    }

}
