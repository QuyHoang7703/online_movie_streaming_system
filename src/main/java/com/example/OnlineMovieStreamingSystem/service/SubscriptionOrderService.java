package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.subscriptionOrder.SubscriptionOrderRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionOrder.PaymentUrlResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionOrder.SubscriptionOrderResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface SubscriptionOrderService {
    PaymentUrlResponseDTO getPaymentUrl(HttpServletRequest request, SubscriptionOrderRequestDTO subscriptionOrderRequestDTO);
    void handlePaymentSuccess(String transactionCode);
    SubscriptionOrderResponseDTO getSubscriptionOrderDetailByTransactionCode(String transactionCode);
    SubscriptionOrderResponseDTO getActiveLatestSubscriptionOrderForUser(long subscriptionPlanId);
    ResultPaginationDTO getSubscriptionOrders(int size, int page);
}
