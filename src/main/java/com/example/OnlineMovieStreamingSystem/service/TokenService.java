package com.example.OnlineMovieStreamingSystem.service;

import java.time.Duration;
import java.time.Instant;

public interface TokenService {
    void storeRefreshToken(String username, String refreshToken, long expiration);
    String getRefreshToken(String username);
    void deleteRefreshToken(String username);
    void blacklistAccessToken(String jti, Duration expiration);
    boolean isAccessTokenBlacklisted(String jti);

}
