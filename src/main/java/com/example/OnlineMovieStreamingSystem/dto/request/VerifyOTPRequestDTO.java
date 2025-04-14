package com.example.OnlineMovieStreamingSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class VerifyOTPRequestDTO {
    @NotBlank(message = "Message cannot be left blank")
    private String email;
    @NotBlank(message = "OTP cannot be left blank")
    private String otp;
}
