package com.example.OnlineMovieStreamingSystem.config;

import com.example.OnlineMovieStreamingSystem.util.SecurityUtil;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.*;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthChannelInterceptorAdapter implements ChannelInterceptor {

    private final SecurityUtil securityUtil;

    @Override
    public Message<?> preSend(@Nonnull Message<?> message, @Nonnull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor != null ? accessor.getCommand() : null)) {
            String token = (String) accessor.getSessionAttributes().get("Authorization");
//            String token = accessor.getNativeHeader("Authorization").get(0);

            if (token == null) {
                throw new ApplicationException("Thiếu Authorization header");
            }

            Authentication user = authenticateUser(token);

            if (user != null) {
                accessor.setUser(user);
                log.info("✅ Xác thực WebSocket thành công cho user: {}", user.getName());
            } else {
                throw new ApplicationException("❌ Token không hợp lệ hoặc hết hạn");
            }
        }

        return message;
    }

    private Authentication authenticateUser(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Bỏ "Bearer "
        } else {
            return null;
        }

        try {
            var decodedJwt = securityUtil.checkValidRefreshToken(token); // hoặc checkValidAccessToken
            String username = decodedJwt.getClaimAsString("sub");

            return new UsernamePasswordAuthenticationToken(
                    username, null, List.of((GrantedAuthority) () -> "ROLE_USER")
            );
        } catch (Exception e) {
            log.warn("Lỗi xác thực JWT: {}", e.getMessage());
            return null;
        }
    }
}
