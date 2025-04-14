package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final RedisTemplate<String, String> redisTemplate;

    private final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private final String BLACKLIST_TOKEN_PREFIX = "blacklist_token:";
    @Override
    public void storeRefreshToken(String username, String refreshToken, long expiration) {
        String key = REFRESH_TOKEN_PREFIX + username;
        redisTemplate.opsForValue().set(key, refreshToken, expiration, TimeUnit.SECONDS);

    }

    @Override
    public String getRefreshToken(String username) {
        String key = REFRESH_TOKEN_PREFIX + username;
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteRefreshToken(String username) {
        String key = REFRESH_TOKEN_PREFIX + username;
        redisTemplate.delete(key);
    }

    @Override
    public void blacklistAccessToken(String jti, Duration expiration) {
        String key = BLACKLIST_TOKEN_PREFIX + jti;
        redisTemplate.opsForValue().set(key, "blacklisted", expiration);
    }

    @Override
    public boolean isAccessTokenBlacklisted(String jti) {
        String key = BLACKLIST_TOKEN_PREFIX + jti;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }


}
