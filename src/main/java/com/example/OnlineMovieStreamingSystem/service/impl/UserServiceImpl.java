package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.user.User;
import com.example.OnlineMovieStreamingSystem.domain.user.UserDetail;
import com.example.OnlineMovieStreamingSystem.dto.UserInfoDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.AuthResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.UserRepository;
import com.example.OnlineMovieStreamingSystem.service.UserService;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public void checkActiveUser(String email)  {
        User user = this.findUserByEmail(email);
        if(!user.isActive()){
            throw new ApplicationException("User is not active");
        }
    }

    @Override
    public AuthResponseDTO convertToLoginResponseDTO(String email) {
        User user = this.findUserByEmail(email);
        AuthResponseDTO authResponseDTO = new AuthResponseDTO();

        UserInfoDTO userInfo = new UserInfoDTO();
        userInfo.setEmail(user.getEmail());
        userInfo.setRole(user.getRole().getName());

        UserDetail userDetail = user.getUserDetail();
        if(userDetail != null){
            userInfo.setName(userDetail.getName());
            userInfo.setAvatarUrl(userDetail.getAvatarUrl());
        }

        authResponseDTO.setUserInfo(userInfo);

        return authResponseDTO;
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }


    private User findUserByEmail(String email)  {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("User not exists with email: " + email));
    }
}
