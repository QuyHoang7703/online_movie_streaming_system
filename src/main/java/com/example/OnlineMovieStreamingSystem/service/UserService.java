package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.UserInfoDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.auth.ChangePasswordRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.user.UpdatePasswordRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.user.UserRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.AuthResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.user.UserResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    void checkActiveUser(String email);
    AuthResponseDTO convertToLoginResponseDTO(String email);
    boolean existsByEmail(String email);
    UserInfoDTO getUserInfo();
    UserResponseDTO updateUserInfo(UserRequestDTO userRequestDTO, MultipartFile avatar) throws IOException;
    void handleUpdatePassword(UpdatePasswordRequestDTO updatePasswordRequestDTO);
}
