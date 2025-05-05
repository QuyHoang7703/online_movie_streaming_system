package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.client.OutboundIdentityClient;
import com.example.OnlineMovieStreamingSystem.client.OutboundUserInfoClient;
import com.example.OnlineMovieStreamingSystem.domain.user.Role;
import com.example.OnlineMovieStreamingSystem.domain.user.User;
import com.example.OnlineMovieStreamingSystem.domain.user.UserDetail;
import com.example.OnlineMovieStreamingSystem.dto.request.oauth2.ExchangeTokenRequest;
import com.example.OnlineMovieStreamingSystem.dto.response.AuthResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.oauth2.ExchangeTokenResponse;
import com.example.OnlineMovieStreamingSystem.dto.response.oauth2.OutboundUserResponse;
import com.example.OnlineMovieStreamingSystem.repository.RoleRepository;
import com.example.OnlineMovieStreamingSystem.repository.UserRepository;
import com.example.OnlineMovieStreamingSystem.service.AuthService;
import com.example.OnlineMovieStreamingSystem.service.Oauth2Service;
import com.example.OnlineMovieStreamingSystem.service.TokenService;
import com.example.OnlineMovieStreamingSystem.service.UserService;
import com.example.OnlineMovieStreamingSystem.util.SecurityUtil;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Oauth2ServiceImpl implements Oauth2Service {
    private final OutboundIdentityClient outboundIdentityClient;
    private final OutboundUserInfoClient outboundUserInfoClient;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final TokenService tokenService;
    private final AuthService authService;

    @Value("${outbound.identity.client-id}")
    protected String CLIENT_ID ;

    @Value("${outbound.identity.client-secret}")
    protected String CLIENT_SECRET ;


    @Value("${outbound.identity.redirect-uri}")
    protected String REDIRECT_URI;

    protected final String GRANT_TYPE = "authorization_code";

    @Value("${datn.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    @Override
    public AuthResponseDTO handleLoginWithClient(String code) {
        String accessTokenFromClient = this.getAccessToken(code);
        User user = this.processOutboundLogin(accessTokenFromClient);
        if (user == null) {
            throw  new ApplicationException("Có lỗi xảy ra");
        }

        AuthResponseDTO authResponseDTO = this.userService.convertToLoginResponseDTO(user.getEmail());
        this.authService.generateAndAttachTokens(authResponseDTO);

        return authResponseDTO;
    }

    private String getAccessToken(String code) {
        ExchangeTokenRequest exchangeTokenRequest = ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build();

        ExchangeTokenResponse exchangeTokenResponse = this.outboundIdentityClient.exchangeToken(exchangeTokenRequest);
        return exchangeTokenResponse.getAccessToken();
    }

    private User processOutboundLogin(String accessToken) {
        OutboundUserResponse outboundUserResponse = this.outboundUserInfoClient.getUserInfo("json", accessToken);
        if(outboundUserResponse != null) {
            String email = outboundUserResponse.getEmail();
            User user = this.userRepository.findByEmail(email).orElse(null);
            if(user == null) {
                user = new User();
                user.setEmail(email);
                user.setActive(true);

                UserDetail userDetail = new UserDetail();
                StringBuilder fullName = new StringBuilder();
                if(outboundUserResponse.getFamilyName() != null) {
                    fullName.append(outboundUserResponse.getFamilyName());
                }
                if(outboundUserResponse.getGivenName() != null) {
                    fullName.append(outboundUserResponse.getGivenName());
                }
                if(outboundUserResponse.getName() != null) {
                    fullName.append(outboundUserResponse.getName());
                }
                userDetail.setName(fullName.toString());
                userDetail.setAddress(outboundUserResponse.getLocale());
                userDetail.setAvatarUrl(outboundUserResponse.getPicture());
                userDetail.setUser(user);

                user.setUserDetail(userDetail);

                Role role = this.roleRepository.findByName("USER");
                user.setRole(role);

                return this.userRepository.save(user);
            }
            return user;
        }
        return null;
    }


}
