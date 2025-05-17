package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.user.Role;
import com.example.OnlineMovieStreamingSystem.domain.user.User;
import com.example.OnlineMovieStreamingSystem.dto.request.LoginRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.RegisterRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.VerifyOTPRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.auth.ChangePasswordRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.AuthResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.RoleRepository;
import com.example.OnlineMovieStreamingSystem.repository.UserRepository;
import com.example.OnlineMovieStreamingSystem.service.AuthService;
import com.example.OnlineMovieStreamingSystem.service.EmailService;
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
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

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
    private final String FORGET_PASSWORD_PREFIX = "forget_password:";
    private final String FORGOT_PASSWORD_COOLDOWN_PREFIX = "reset_password_request:";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;


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

        log.info("Authentication Principal: " + authentication.getPrincipal());
        log.info("Email: " + authentication.getName());
        AuthResponseDTO authResponseDTO = this.userService.convertToLoginResponseDTO(email);

//        // Create access token, refresh token
//        String accessToken = this.securityUtil.createAccessToken(email, authResponseDTO.getUserInfo());
//        String refreshToken = this.securityUtil.createRefreshToken(email, authResponseDTO.getUserInfo());
//
//        authResponseDTO.setAccessToken(accessToken);
//        authResponseDTO.setRefreshToken(refreshToken);
//
//        this.tokenService.storeRefreshToken(email, refreshToken, refreshTokenExpiration);
        this.generateAndAttachTokens(authResponseDTO);

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

        this.generateAndAttachTokens(authResponseDTO);

        return authResponseDTO;

    }

    @Override
    public void generateAndAttachTokens(AuthResponseDTO authResponseDTO) {
        String email = authResponseDTO.getUserInfo().getEmail();
        // Create new access token, refresh token
        String newAccessToken = this.securityUtil.createAccessToken(email, authResponseDTO.getUserInfo());
        String newRefreshToken = this.securityUtil.createRefreshToken(email, authResponseDTO.getUserInfo());

        authResponseDTO.setAccessToken(newAccessToken);
        authResponseDTO.setRefreshToken(newRefreshToken);

        // Update new refresh token in redis
        this.tokenService.storeRefreshToken(email, newRefreshToken, refreshTokenExpiration);

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

        }
//        else{
//            throw new ApplicationException("No token in header received");
//        }
    }

    @Override
    public void handleRegisterUser(RegisterRequestDTO registerRequestDTO) throws IOException {
        String email = registerRequestDTO.getEmail();
        boolean isAvailableEmail = this.userService.existsByEmail(email);
        if(isAvailableEmail) {
            throw new ApplicationException("Email already in use");
        }
        if(!registerRequestDTO.getPassword().equals(registerRequestDTO.getConfirmPassword())) {
            throw new ApplicationException("Passwords do not match");
        }

        String key = REGISTER_PREFIX + email;
        String encodedPassword = this.passwordEncoder.encode(registerRequestDTO.getPassword());

        HashOperations<String, String , String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(key, "email",email);
        hashOperations.put(key, "password", encodedPassword);
        String otp = this.generateOTP();
        hashOperations.put(key, "otp", otp);
        hashOperations.put(key, "createTime", Instant.now().toString());
        redisTemplate.expire(key, Duration.ofMinutes(5));

        // Send otp through email
        this.sendVerificationEmail(email, otp);

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

    @Override
    public void handleResendOtp(String email) throws IOException {
        String key = REGISTER_PREFIX + email;
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        String createTimeStr = hashOperations.get(key, "createTime");
        if(createTimeStr == null) {
            throw new ApplicationException("OTP is expired");
        }

        Instant createTime = Instant.parse(createTimeStr);
        Instant now = Instant.now();

        if(Duration.between(createTime, now).getSeconds() < 60) {
            throw new ApplicationException("Just resend new OTP after 1 minutes");
        }

        String otp = this.generateOTP();
        hashOperations.put(key, "otp", otp);
        hashOperations.put(key, "createTime", now.toString());
        redisTemplate.expire(key, Duration.ofMinutes(5));

        // Send otp through email
        this.sendVerificationEmail(email, otp);

    }

    @Override
    public void createTokenForResetPassword(String email) {
        String token = UUID.randomUUID().toString();

        String key = FORGET_PASSWORD_PREFIX + token;
        String cooldownKey = FORGOT_PASSWORD_COOLDOWN_PREFIX + email;

        boolean isAvailableRequest = Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey));

        if (isAvailableRequest) {
            throw new ApplicationException("Vui lòng đợi ít phút để gửi lại yêu cầu");
        }

        redisTemplate.opsForValue().set(key, email, Duration.ofMinutes(5));
        log.info("Token for reset password: " + token);

        // Create cooldown key for the email
        redisTemplate.opsForValue().set(cooldownKey, email, Duration.ofMinutes(2));

        // Send link through email
        this.sendForgotPasswordRequest(email, token);
    }

    @Override
    public boolean isValidToken(String token) {
        String key = FORGET_PASSWORD_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void handleResetPassword(ChangePasswordRequestDTO changePasswordRequestDTO) {
        String key = FORGET_PASSWORD_PREFIX + changePasswordRequestDTO.getToken();
        if(Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            throw new ApplicationException("Yêu cầu của bạn đã hết hạn vui lòng thử lại");
        }
        if(!changePasswordRequestDTO.getConfirmPassword().equals(changePasswordRequestDTO.getPassword())) {
            throw new ApplicationException("Mật khẩu không trùng khớp");
        }

        String email = redisTemplate.opsForValue().get(key);
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("Không tồn tại tài khoản với email này"));
        String decodedPassword = this.passwordEncoder.encode(changePasswordRequestDTO.getPassword());
        user.setPassword(decodedPassword);
        this.userRepository.save(user);

        // Delete token in redis
        redisTemplate.delete(key);

        // Delete cooldown key
        String cooldownKey = FORGOT_PASSWORD_COOLDOWN_PREFIX + email;
        if(Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
            redisTemplate.delete(cooldownKey);
        }

    }

    private String generateOTP() {
        Random random = new Random();
        int otpValue = 100000 + random.nextInt(900000);

        return String.valueOf(otpValue);
    }

    public void sendVerificationEmail(String email, String otp) throws IOException {
        String subject = "EMovie - Xác nhận email";
        Context context = new Context();
        context.setVariable("otp", otp);
        this.emailService.sendEmail(email, subject, "otp-email", context);
    }

    private void sendForgotPasswordRequest(String email, String token) {
        String subject = "Đặt lại mật khẩu";

        // Tạo liên kết chứa token để người dùng nhấn vào => chuyển đến fe xử lý
        String resetPasswordLink = "http://localhost:5173/reset-password?token=" + token;

        // Tạo nội dung email từ template HTML
        Context context = new Context();
        context.setVariable("resetPasswordLink", resetPasswordLink);

        this.emailService.sendEmail(email, subject, "reset-password", context);

    }


}
