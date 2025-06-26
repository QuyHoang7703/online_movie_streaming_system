package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.user.User;
import com.example.OnlineMovieStreamingSystem.domain.user.UserDetail;
import com.example.OnlineMovieStreamingSystem.dto.UserInfoDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.user.UpdatePasswordRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.user.UserRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.AuthResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.user.UserResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.UserDetailRepository;
import com.example.OnlineMovieStreamingSystem.repository.UserRepository;
import com.example.OnlineMovieStreamingSystem.service.ImageStorageService;
import com.example.OnlineMovieStreamingSystem.service.UserService;
import com.example.OnlineMovieStreamingSystem.util.SecurityUtil;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;
    private final String CONTAINER_NAME = "users-image-container";
    private final UserDetailRepository userDetailRepository;
    private final PasswordEncoder passwordEncoder;
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

    @Override
    public UserResponseDTO updateUserInfo(UserRequestDTO userRequestDTO, MultipartFile avatar) throws IOException {
        String email = SecurityUtil.getLoggedEmail();
        User currentUser = this.findUserByEmail(email);
        UserDetail userDetail = currentUser.getUserDetail();
        if(userRequestDTO.getName() != null && !Objects.equals(userRequestDTO.getName(), userDetail.getName())) {
            userDetail.setName(userRequestDTO.getName());
        }
        if(userRequestDTO.getPhoneNumber() != null && !Objects.equals(userDetail.getPhoneNumber(), userRequestDTO.getPhoneNumber())) {
            userDetail.setPhoneNumber(userRequestDTO.getPhoneNumber());
        }
        if(userRequestDTO.getGender() != null && !Objects.equals(userDetail.getGender(), userRequestDTO.getGender())) {
            userDetail.setGender(userRequestDTO.getGender());
        }
        if(userRequestDTO.getAddress() != null && !Objects.equals(userDetail.getAddress(), userRequestDTO.getAddress())) {
            userDetail.setAddress(userRequestDTO.getAddress());
        }

        String avatarUrl = userDetail.getAvatarUrl();
        if(avatar != null && avatarUrl != null){
            String newAvatarUrl = this.imageStorageService.uploadFile(CONTAINER_NAME, avatar.getOriginalFilename(), avatar.getInputStream());
            // After uploaded successfully then delete old avatar
            String originalAvatarName = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
            this.imageStorageService.deleteFile(CONTAINER_NAME, originalAvatarName);
            userDetail.setAvatarUrl(newAvatarUrl);
        }else{
            log.info("Not avatar to update");
        }

        UserDetail updatedUserDetail = this.userDetailRepository.save(userDetail);


        return this.convertToUserResponseDTO(updatedUserDetail);
    }

    @Override
    public void handleUpdatePassword(UpdatePasswordRequestDTO updatePasswordRequestDTO) {
        String email = SecurityUtil.getLoggedEmail();
        if(!updatePasswordRequestDTO.getPassword().equals(updatePasswordRequestDTO.getConfirmPassword())) {
            throw new ApplicationException("Mật khẩu không trùng khớp");
        }
        User user = this.findUserByEmail(email);

        if(!this.passwordEncoder.matches(updatePasswordRequestDTO.getCurrentPassword(), user.getPassword())) {
            throw new ApplicationException("Mật khẩu hiện tại không đúng");
        }
        String decodedPassword = this.passwordEncoder.encode(updatePasswordRequestDTO.getPassword());
        user.setPassword(decodedPassword);
        this.userRepository.save(user);

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
            userInfoDTO.setPhoneNumber(userDetail.getPhoneNumber());
            userInfoDTO.setGender(userDetail.getGender());
            userInfoDTO.setAddress(userDetail.getAddress());
        }
        return userInfoDTO;
    }

    private UserResponseDTO convertToUserResponseDTO(UserDetail userDetail) {
        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .id(userDetail.getUser().getId())
                .email(userDetail.getUser().getEmail())
                .name(userDetail.getName())
                .phoneNumber(userDetail.getPhoneNumber())
                .gender(userDetail.getGender())
                .address(userDetail.getAddress())
                .avatarUrl(userDetail.getAvatarUrl())
                .role(userDetail.getUser().getRole().getName())
                .build();

        return userResponseDTO;
    }
}
