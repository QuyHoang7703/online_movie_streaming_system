package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.response.AuthResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.Oauth2Service;
import com.example.OnlineMovieStreamingSystem.util.SecurityUtil;
import com.example.OnlineMovieStreamingSystem.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth/oauth2")
@Slf4j
public class Oauth2Controller {
    private final Oauth2Service oauth2Service;
    private final SecurityUtil securityUtil;

    @Value("${datn.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${datn.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    @PostMapping("/login/google")
    @ApiMessage("Đăng nhập thành công")
    public ResponseEntity<AuthResponseDTO> loginWithGoogle(@RequestParam("code") String code) {
        log.info("code: " +code);
        AuthResponseDTO authResponseDTO = this.oauth2Service.handleLoginWithClient(code);
        String accessToken = authResponseDTO.getAccessToken();
        String refreshToken = authResponseDTO.getRefreshToken();
        ResponseCookie accessTokenCookies = this.securityUtil.createTokenCookie("access_token", accessToken, accessTokenExpiration);
        ResponseCookie refreshTokenCookies = this.securityUtil.createTokenCookie("refresh_token", refreshToken, refreshTokenExpiration);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accessTokenCookies.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookies.toString())
                .body(authResponseDTO);
    }


}
