package com.example.OnlineMovieStreamingSystem.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequestDTO {
    @NotBlank(message = "Token cannot be left blank")
    private String token;
    @NotBlank(message = "Password cannot be left blank")
    private String password;
    @NotBlank(message = "Confirm password cannot be left blank")
    private String confirmPassword;
}
