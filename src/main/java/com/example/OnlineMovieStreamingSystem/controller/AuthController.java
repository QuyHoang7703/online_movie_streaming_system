package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.request.LoginRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.AuthResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.AuthService;
import com.example.OnlineMovieStreamingSystem.service.TokenService;
import com.example.OnlineMovieStreamingSystem.service.UserService;
import com.example.OnlineMovieStreamingSystem.util.SecurityUtil;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final TokenService tokenService;
    private final AuthService authService;

    @Value("${datn.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${datn.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        AuthResponseDTO authResponseDTO = this.authService.loginAuthToken(loginRequestDTO);

        String accessToken = authResponseDTO.getAccessToken();
        String refreshToken = authResponseDTO.getRefreshToken();

        authResponseDTO.setAccessToken(accessToken);

        ResponseCookie accessTokenCookies = this.securityUtil.createTokenCookie("access_token", accessToken, accessTokenExpiration);
        ResponseCookie refreshTokenCookies = this.securityUtil.createTokenCookie("refresh_token", refreshToken, refreshTokenExpiration);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accessTokenCookies.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookies.toString())
                .body(authResponseDTO);

    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> getRefreshToken(@CookieValue(name="refresh_token", defaultValue = "noRefreshTokenInCookie") String refreshToken) {
        AuthResponseDTO authResponseDTO = this.authService.refreshAuthToken(refreshToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, authResponseDTO.getAccessToken())
                .header(HttpHeaders.SET_COOKIE, authResponseDTO.getRefreshToken())
                .body(authResponseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        this.authService.logoutAuthToken();

        ResponseCookie accessTokenCookies = this.securityUtil.createTokenCookie("access_token", null, 0 );
        ResponseCookie refreshTokenCookies = this.securityUtil.createTokenCookie("refresh_token", null, 0 );

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accessTokenCookies.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookies.toString())
                .body(null);
    }

}
