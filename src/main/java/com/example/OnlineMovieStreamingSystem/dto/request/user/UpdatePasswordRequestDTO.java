package com.example.OnlineMovieStreamingSystem.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequestDTO {
    @NotBlank(message = "Current assword cannot be left blank")
    private String currentPassword;
    @NotBlank(message = "Password cannot be left blank")
    private String password;
    @NotBlank(message = "Confirm password cannot be left blank")
    private String confirmPassword;
}
