package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.dto.response.statistic.GenreStatisticDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.statistic.MonthlyRevenueResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.statistic.OverviewStatisticResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.statistic.YearRevenueDTO;
import com.example.OnlineMovieStreamingSystem.repository.GenreRepository;
import com.example.OnlineMovieStreamingSystem.repository.MovieRepository;
import com.example.OnlineMovieStreamingSystem.repository.SubscriptionOrderRepository;
import com.example.OnlineMovieStreamingSystem.repository.UserRepository;
import com.example.OnlineMovieStreamingSystem.service.StatisticService;
import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final SubscriptionOrderRepository subscriptionOrderRepository;
    private final GenreRepository genreRepository;

    @Override
    public OverviewStatisticResponseDTO getOverviewStatistic(int month, int year) {
        long totalUsers = this.userRepository.countByRoleName("USER");
        long standaloneMovies = this.movieRepository.countByMovieType(MovieType.STANDALONE);
        long seriesMovies = this.movieRepository.countByMovieType(MovieType.SERIES);

        Double currentMonthRevenue = this.subscriptionOrderRepository.getRevenueByMonth(month, year);

        OverviewStatisticResponseDTO overviewStatisticResponseDTO = OverviewStatisticResponseDTO.builder()
                .totalUsers(totalUsers)
                .standaloneMovies(standaloneMovies)
                .seriesMovies(seriesMovies)
                .build();
        if (currentMonthRevenue != null) {
            overviewStatisticResponseDTO.setCurrentMonthRevenue(currentMonthRevenue);
        }
        return overviewStatisticResponseDTO;
    }

    @Override
    public List<MonthlyRevenueResponseDTO> getMonthlyRevenue(int year) {
        List<MonthlyRevenueResponseDTO> rawRevenue = this.subscriptionOrderRepository.getMonthlyRevenue(year);
        // Đưa vào Map để dễ kiểm tra
        Map<Integer, Double> revenueMap = rawRevenue.stream()
                .collect(Collectors.toMap(MonthlyRevenueResponseDTO::getMonth, MonthlyRevenueResponseDTO::getRevenue));

        List<MonthlyRevenueResponseDTO> fullResult = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Double revenue = revenueMap.getOrDefault(month, 0.0);
            fullResult.add(new MonthlyRevenueResponseDTO(month, revenue));
        }
        return fullResult;

    }

    @Override
    public List<GenreStatisticDTO> getGenreStatistic() {
        List<GenreStatisticDTO> genreStatisticDTOS = this.genreRepository.getGenreStatistics();

        return genreStatisticDTOS;
    }

    @Override
    public List<YearRevenueDTO> getYearRevenue(int startYear, int endYear) {
        List<YearRevenueDTO> rawRevenue = this.subscriptionOrderRepository.getYearRevenue(startYear, endYear);
        Map<Integer, Double> revenueMap = rawRevenue.stream()
                .collect(Collectors.toMap(YearRevenueDTO::getYear, YearRevenueDTO::getRevenue));

        List<YearRevenueDTO> fullResult = new ArrayList<>();
        for (int year = startYear; year <= endYear; year++) {
            Double revenue = revenueMap.getOrDefault(year, 0.0);
            fullResult.add(new YearRevenueDTO(year, revenue));
        }

        return fullResult;

    }
}
