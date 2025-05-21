package com.example.OnlineMovieStreamingSystem.dto.request.subscriptionOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionOrderRequestDTO {
    private Long planDurationId;

    // Các thuộc tính khác của order nếu mở rộng sau này (voucherId)
}
