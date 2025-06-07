package com.example.OnlineMovieStreamingSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegisterRequestDTO {
    @NotBlank(message = "Email cannot be left blank")
    private String email;
    @NotBlank(message = "Name cannot be left blank")
    private String name;
    @NotBlank(message = "Password cannot be left blank")
    @Size(min = 6, message = "Password has to at least 6 characters")
    private String password;
    @NotBlank(message = "Confirm password cannot be left blank")
    private String confirmPassword;
}
