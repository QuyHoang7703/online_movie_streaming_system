package com.example.OnlineMovieStreamingSystem.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {


    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            Cookie[] cookies = httpServletRequest.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("access_token".equals(cookie.getName())) {
                        String token = cookie.getValue();
                        // Lưu vào attributes để AuthChannelInterceptorAdapter dùng
                        attributes.put("Authorization", "Bearer " + token);
                        break;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // không cần xử lý
    }
}

