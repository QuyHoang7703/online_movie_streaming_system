package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.UserInfoDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.user.UpdatePasswordRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.user.UserRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.user.UserResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.UserService;
import com.example.OnlineMovieStreamingSystem.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

    @PatchMapping("/update-info")
    @ApiMessage("Cập nhập thông tin thành công")
    public ResponseEntity<UserResponseDTO> updateUserInfo(@RequestPart("userInfo") UserRequestDTO userRequestDTO,
                                                          @RequestParam(name = "avatar", required = false) MultipartFile avatar) throws IOException {

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.updateUserInfo(userRequestDTO, avatar));
    }

    @PatchMapping("/update-password")
    @ApiMessage("Cập nhập mật khẩu thành công")
    public ResponseEntity<Void> updatePassword(@RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO) {
        this.userService.handleUpdatePassword(updatePasswordRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
