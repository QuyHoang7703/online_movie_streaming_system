package com.example.OnlineMovieStreamingSystem.config;

import com.example.OnlineMovieStreamingSystem.service.TokenService;
import com.example.OnlineMovieStreamingSystem.util.SecurityUtil;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@EnableMethodSecurity(securedEnabled = true)
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfiguration {
    @Value("${datn.jwt.base64-secret}")
    private String jwtKey;

    private final TokenService tokenService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final static String[] WHITE_LIST = {
            "/",
            "/api/v1/auth/**",
            "/identity/auth/outbound/authentication",
            "/api/v1/user/movies/**",
            "/api/v1/countries",
            "/api/v1/genres/all",
            "/api/v1/movies/*/video-versions",
            "/api/v1/video-versions/*/episodes",
            "/api/v1/subscription-orders",
            "/api/v1/vn-pay-callback",
            "/api/v1/user/movies/**",
            "/ws/**",
            "/api/v1/home-page/**",
            "/api/v1/retrain-model/**"

    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtDecoder jwtDecoder,
                                                   JwtAuthenticationConverter jwtAuthenticationConverter,
                                                   CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                                                   CookieToHeaderFilter cookieToHeaderFilter) throws Exception {
        http
                .csrf(c->c.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        authz -> authz
                                .requestMatchers(WHITE_LIST).permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/series-movie/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/standalone-movies/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/episodes/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/actors/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/comments/**").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter))
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .addFilterBefore(cookieToHeaderFilter, BearerTokenAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    //Create signature
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        // Using lamda to create object from functional interface
        return token -> {
            try{
                Jwt decodedJwt = jwtDecoder.decode(token);
                Map<String, Object> claims = decodedJwt.getClaims();
//                for(Map.Entry<String, Object> claim : claims.entrySet()) {
//                    System.out.println(">>>>>> Claim: " + claim.getKey() + " = " + claim.getValue());
//                }
                return decodedJwt;
            }catch (Exception e) {
                log.error("JWT Exception", e);
                throw e;
            }
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Lấy roles từ JWT
            Map<String, Object> userClaims = jwt.getClaimAsMap("user"); // Lấy claims của "user" dưới dạng Map
            if(userClaims != null) {
                String jti = jwt.getId();
                if(tokenService.isAccessTokenBlacklisted(jti)) {
                    throw new JwtException("Access token is blacklisted");
                }
                String roleNameFromJWT = (String) userClaims.get("role");
                List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleNameFromJWT));
                return authorities;
            }
            return Collections.emptyList();
        });
        return jwtAuthenticationConverter;
    }




}
