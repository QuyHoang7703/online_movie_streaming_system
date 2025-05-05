package com.example.OnlineMovieStreamingSystem.client;

import com.example.OnlineMovieStreamingSystem.dto.response.oauth2.OutboundUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="user-info",  url = "https://www.googleapis.com")
public interface OutboundUserInfoClient {
    @GetMapping(value = "/oauth2/v1/userinfo")
    OutboundUserResponse getUserInfo(@RequestParam("alt") String alt, @RequestParam("access_token") String accessToken);
}
