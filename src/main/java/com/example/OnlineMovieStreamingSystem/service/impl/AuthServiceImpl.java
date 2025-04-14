package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.dto.request.LoginRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.AuthResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.AuthService;
import com.example.OnlineMovieStreamingSystem.service.TokenService;
import com.example.OnlineMovieStreamingSystem.service.UserService;
import com.example.OnlineMovieStreamingSystem.util.SecurityUtil;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Value("${datn.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${datn.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final TokenService tokenService;


    @Override
    public AuthResponseDTO loginAuthToken(LoginRequestDTO loginRequestDTO) {
        String email = loginRequestDTO.getEmail();
        this.userService.checkActiveUser(email);
        // Load username and password into security check
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, loginRequestDTO.getPassword());

        // Find suitable provider with token used and in case successfully return authentication object
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Set authentication into Security Context Holder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Authentication: " + authentication);
        log.info("Email: " + authentication.getName());
        AuthResponseDTO authResponseDTO = this.userService.convertToLoginResponseDTO(email);

        // Create access token, refresh token
        String accessToken = this.securityUtil.createAccessToken(email, authResponseDTO.getUserInfo());
        String refreshToken = this.securityUtil.createRefreshToken(email, authResponseDTO.getUserInfo());

        authResponseDTO.setAccessToken(accessToken);
        authResponseDTO.setRefreshToken(refreshToken);

        this.tokenService.storeRefreshToken(email, refreshToken, refreshTokenExpiration);



        return authResponseDTO;
    }

    @Override
    public AuthResponseDTO refreshAuthToken(String refreshToken) {
        if("noRefreshTokenInCookie".equals(refreshToken)) {
            throw new ApplicationException("You don't have refresh token in Cookie");
        }
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();
        String currentRefreshToken = this.tokenService.getRefreshToken(email);
        if(!refreshToken.equals(currentRefreshToken)) {
            throw new ApplicationException("Refresh token is invalid");
        }

        AuthResponseDTO authResponseDTO = this.userService.convertToLoginResponseDTO(email);

        // Create new access token, refresh token
        String newAccessToken = this.securityUtil.createAccessToken(email, authResponseDTO.getUserInfo());
        String newRefreshToken = this.securityUtil.createRefreshToken(email, authResponseDTO.getUserInfo());

        authResponseDTO.setAccessToken(newAccessToken);
        authResponseDTO.setRefreshToken(newRefreshToken);

        // Update new refresh token in redis
        this.tokenService.storeRefreshToken(email, newRefreshToken, refreshTokenExpiration);

        return authResponseDTO;

    }

    @Override
    public void logoutAuthToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt decodedToken) {
            String email = authentication.getName();
            String jti = decodedToken.getId();
            Instant expiration = decodedToken.getExpiresAt();
            Duration duration = Duration.between(Instant.now(), expiration);

            this.tokenService.blacklistAccessToken(jti, duration);
            this.tokenService.deleteRefreshToken(email);

        }else{
            throw new ApplicationException("No token in header received");
        }
    }
}
