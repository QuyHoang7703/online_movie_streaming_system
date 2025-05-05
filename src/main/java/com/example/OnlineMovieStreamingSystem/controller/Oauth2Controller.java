package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.response.AuthResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.Oauth2Service;
import com.example.OnlineMovieStreamingSystem.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth/oauth2")
public class Oauth2Controller {
    private final Oauth2Service oauth2Service;
    @PostMapping("/login/google")
    @ApiMessage("Đăng nhập thành công")
    public ResponseEntity<AuthResponseDTO> loginWithGoogle(@RequestParam("code") String code) {

        return ResponseEntity.status(HttpStatus.OK).body(this.oauth2Service.handleLoginWithClient(code));
    }


}
