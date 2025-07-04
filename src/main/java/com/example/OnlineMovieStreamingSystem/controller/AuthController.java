package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.UserInfoDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.LoginRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.RegisterRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.VerifyOTPRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.auth.ChangePasswordRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.auth.ForgetPasswordRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.AuthResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.AuthService;
import com.example.OnlineMovieStreamingSystem.service.TokenService;
import com.example.OnlineMovieStreamingSystem.service.UserService;
import com.example.OnlineMovieStreamingSystem.util.SecurityUtil;
import com.example.OnlineMovieStreamingSystem.util.annotation.ApiMessage;
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

import java.io.IOException;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final AuthService authService;

    @Value("${datn.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${datn.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        log.info("Login successfully");
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
        log.info("Có refresk token: " + refreshToken);
        AuthResponseDTO authResponseDTO = this.authService.refreshAuthToken(refreshToken);
        ResponseCookie accessTokenCookies = this.securityUtil.createTokenCookie("access_token", authResponseDTO.getAccessToken(), accessTokenExpiration);
        ResponseCookie refreshTokenCookies = this.securityUtil.createTokenCookie("refresh_token", authResponseDTO.getRefreshToken(), refreshTokenExpiration);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accessTokenCookies.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookies.toString())
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

    @PostMapping("/register")
    @ApiMessage("Check email")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) throws IOException {
        this.authService.handleRegisterUser(registerRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/verify-otp")
    @ApiMessage("Verify successfully")
    public ResponseEntity<Void> verifyOTP(@Valid @RequestBody VerifyOTPRequestDTO verifyOTPRequestDTO) {
        this.authService.handleVerifyOTP(verifyOTPRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/resend-otp")
    @ApiMessage("Resent new OTP")
    public ResponseEntity<Void> resendOTP(@RequestParam String email) throws IOException {
        this.authService.handleResendOtp(email);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/get-auth-user")
    public ResponseEntity<UserInfoDTO> getUserInfo(@CookieValue(name="access_token", defaultValue = "noRefreshTokenInCookie") String accessToken) {
        System.out.println(">>>>>>>> Access token from cookie: " + accessToken);
        UserInfoDTO userInfoDTO = this.userService.getUserInfo();
        return ResponseEntity.status(HttpStatus.OK).body(userInfoDTO);
    }

    @PostMapping("/forgot-password")
    @ApiMessage("Đã gửi yêu cầu qua email")
    public ResponseEntity<Void> sendRequestForgotPassword(@RequestBody @Valid ForgetPasswordRequestDTO forgetPasswordRequestDTO) {
        this.authService.createTokenForResetPassword(forgetPasswordRequestDTO.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/verify-token")
    public ResponseEntity<Boolean> verifyToken(@RequestParam String token) {

        return ResponseEntity.status(HttpStatus.OK).body(this.authService.isValidToken(token));
    }

    @PostMapping("/reset-password")
    @ApiMessage("Thay đổi mật khẩu thành công")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ChangePasswordRequestDTO changePasswordRequestDTO) {
        this.authService.handleResetPassword(changePasswordRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    

}
