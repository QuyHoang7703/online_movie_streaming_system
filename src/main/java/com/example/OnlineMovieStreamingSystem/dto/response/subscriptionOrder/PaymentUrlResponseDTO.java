package com.example.OnlineMovieStreamingSystem.dto.response.subscriptionOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentUrlResponseDTO {
    public String message;
    private String paymentUrl;
}
