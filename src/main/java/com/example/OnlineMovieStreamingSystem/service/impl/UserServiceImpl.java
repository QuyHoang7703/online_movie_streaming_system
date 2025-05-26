package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.user.User;
import com.example.OnlineMovieStreamingSystem.domain.user.UserDetail;
import com.example.OnlineMovieStreamingSystem.dto.UserInfoDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.AuthResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.UserRepository;
import com.example.OnlineMovieStreamingSystem.service.UserService;
import com.example.OnlineMovieStreamingSystem.util.SecurityUtil;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

//        UserInfoDTO userInfo = new UserInfoDTO();
//        userInfo.setEmail(user.getEmail());
//        userInfo.setRole(user.getRole().getName());
//
//        UserDetail userDetail = user.getUserDetail();
//        if(userDetail != null){
//            userInfo.setName(userDetail.getName());
//            userInfo.setAvatarUrl(userDetail.getAvatarUrl());
//        }
        UserInfoDTO userInfoDTO = this.convertToUserInfoDTO(user);
        authResponseDTO.setUserInfo(userInfoDTO);

        return authResponseDTO;
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Override
    public UserInfoDTO getUserInfo() {
//        String email = SecurityUtil.getCurrentLogin().isPresent() ? SecurityUtil.getCurrentLogin().get() : null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        System.out.println(">>>>>>> Email: " + email);
        if(email == null) {
            throw new ApplicationException("You must login first");
        }

        User currentUser = this.findUserByEmail(email);
        UserInfoDTO userInfoDTO = this.convertToUserInfoDTO(currentUser);

        return userInfoDTO;
    }


    private User findUserByEmail(String email)  {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("User not exists with email: " + email));
    }

    private UserInfoDTO convertToUserInfoDTO(User user) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(user.getId());
        userInfoDTO.setEmail(user.getEmail());
        userInfoDTO.setRole(user.getRole().getName());

        UserDetail userDetail = user.getUserDetail();
        if(userDetail != null){
            userInfoDTO.setName(userDetail.getName());
            userInfoDTO.setAvatarUrl(userDetail.getAvatarUrl());
        }
        return userInfoDTO;
    }
}
