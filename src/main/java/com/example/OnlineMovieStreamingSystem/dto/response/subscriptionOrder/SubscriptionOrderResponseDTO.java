package com.example.OnlineMovieStreamingSystem.dto.response.subscriptionOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionOrderResponseDTO {
    private long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private double price;
    private String transactionCode;
    private Instant createAt;
    private String subscriptionPlanName;
    private int durationInMonths;
    private String name;
    private String email;
    private String phoneNumber;
}
