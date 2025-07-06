package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.service.MovieRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieRedisServiceImpl implements MovieRedisService {
//    private static final String MOVIE_KEY = "movie:";
    private final RedisTemplate<String, Object> redisTemplate;
    @Override
    public String getKey(List<String> genreNames,
                         String movieType,
                         List<String> countries,
                         Long subscriptionPlanId,
                         int page, int size, boolean isAdmin) {
        String movieKey = String.format("movie:%s:genres=%s&movieType=%s&countries=%s&subscriptionPlanId=%s&page=%d&size=%d",
                isAdmin ? "admin" : "user",
                genreNames != null ? String.join(", ", genreNames) : "all",
                movieType != null ? movieType : "all",
                countries != null ? String.join(", ", countries) : "all",
                subscriptionPlanId != null ? subscriptionPlanId : "all",
                page,
                size);

        return movieKey;
    }

    @Override
    public void clearMovieInRedis() {
        Set<String> keys = redisTemplate.keys("movie:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }


}
