package com.example.OnlineMovieStreamingSystem.util;

import com.example.OnlineMovieStreamingSystem.dto.UserInfoDTO;
import com.nimbusds.jose.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityUtil {
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
    private final JwtEncoder jwtEncoder;

    @Value("${datn.jwt.base64-secret}")
    private String jwtKey;

    @Value("${datn.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${datn.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }

    public String createAccessToken(String username, UserInfoDTO userInfoDTO) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

        // Create Header
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        //Create Payload
        String jti = UUID.randomUUID().toString();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .id(jti)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(username)
                .claim("user", userInfoDTO)
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String createRefreshToken(String username, UserInfoDTO userInfoDTO) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);

        // Create Header
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        //Create Payload
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(username)
                .claim("user", userInfoDTO)
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public Jwt checkValidRefreshToken(String token) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        try {
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            log.error(">>> JWT error: " + e.getMessage());
            throw e;
        }

    }

    public ResponseCookie createTokenCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)   // HTTP-only for security
                .secure(true)     // Secure flag
                .path("/")        // Cookie valid for entire site
                .maxAge(maxAge)   // Expiration time\
//                .domain("localhost")
                .sameSite("None")
                .build();
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static Optional<String> getCurrentLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

    public static String getJti () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) {
            return null;
        } else if (authentication.getPrincipal() instanceof Jwt jwt){
            return jwt.getClaim("jti");
        }
        return null;
    }




}
