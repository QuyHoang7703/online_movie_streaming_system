package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.response.statistic.GenreStatisticDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.statistic.MonthlyRevenueResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.statistic.OverviewStatisticResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.statistic.YearRevenueDTO;

import java.util.List;

public interface StatisticService {
    OverviewStatisticResponseDTO getOverviewStatistic(int month, int year);
    List<MonthlyRevenueResponseDTO> getMonthlyRevenue(int year);
    List<GenreStatisticDTO> getGenreStatistic();
    List<YearRevenueDTO> getYearRevenue(int startYear, int endYear);

}
