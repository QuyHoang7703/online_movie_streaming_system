package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.user.Role;
import com.example.OnlineMovieStreamingSystem.domain.user.User;
import com.example.OnlineMovieStreamingSystem.dto.request.LoginRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.RegisterRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.VerifyOTPRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.AuthResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.RoleRepository;
import com.example.OnlineMovieStreamingSystem.repository.UserRepository;
import com.example.OnlineMovieStreamingSystem.service.AuthService;
import com.example.OnlineMovieStreamingSystem.service.TokenService;
import com.example.OnlineMovieStreamingSystem.service.UserService;
import com.example.OnlineMovieStreamingSystem.util.SecurityUtil;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final HashOperations hashOperations;
    @Value("${datn.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${datn.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final TokenService tokenService;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final String REGISTER_PREFIX = "register:";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


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

    @Override
    public void handleRegisterUser(RegisterRequestDTO registerRequestDTO) {
        boolean isAvailableEmail = this.userService.existsByEmail(registerRequestDTO.getEmail());
        if(isAvailableEmail) {
            throw new ApplicationException("Email already in use");
        }
        if(!registerRequestDTO.getPassword().equals(registerRequestDTO.getConfirmPassword())) {
            throw new ApplicationException("Passwords do not match");
        }

        String key = REGISTER_PREFIX + registerRequestDTO.getEmail();
        String encodedPassword = this.passwordEncoder.encode(registerRequestDTO.getPassword());

        HashOperations<String, String , String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(key, "email", registerRequestDTO.getEmail());
        hashOperations.put(key, "password", encodedPassword);
        hashOperations.put(key, "otp", this.generateOTP());
        redisTemplate.expire(key, Duration.ofMinutes(5));

    }

    @Override
    public void handleVerifyOTP(VerifyOTPRequestDTO verifyOTPRequestDTO) {
        String key = REGISTER_PREFIX + verifyOTPRequestDTO.getEmail();
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        String email = hashOperations.get(key, "email");
        if(email == null) {
            throw new ApplicationException("OTP expired");
        }

        String redisOtp = hashOperations.get(key, "otp");
        if(redisOtp == null || !redisOtp.equals(verifyOTPRequestDTO.getOtp())) {
            throw new ApplicationException("OTP is incorrect");
        }

        String password = hashOperations.get(key, "password");

        redisTemplate.delete(key);

        Role role = this.roleRepository.findByName("USER");
        User user = User.builder()
                .email(email)
                .password(password)
                .active(true)
                .role(role)
                .build();

        this.userRepository.save(user);
    }

    private String generateOTP() {
        Random random = new Random();
        int otpValue = 100000 + random.nextInt(900000);

        return String.valueOf(otpValue);
    }
}
