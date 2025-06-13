package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.config.VnPayConfig;
import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.subscriptionOrder.SubscriptionOrderRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionOrder.PaymentUrlResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionOrder.SubscriptionOrderResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.SubscriptionOrderService;
import com.example.OnlineMovieStreamingSystem.util.VnPayUtil;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionOrderController {
    private final SubscriptionOrderService subscriptionOrderService;
    private final VnPayConfig vnPayConfig;

    @PostMapping("/subscription-orders")
    public ResponseEntity<PaymentUrlResponseDTO> getPayUrl(HttpServletRequest request, @RequestBody SubscriptionOrderRequestDTO subscriptionOrderRequestDTO) {

        return ResponseEntity.status(HttpStatus.OK).body(this.subscriptionOrderService.getPaymentUrl(request,subscriptionOrderRequestDTO));
    }

    @GetMapping("/vn-pay-callback")
    public ResponseEntity<Void> payCallbackHandler(HttpServletRequest request) {
        boolean isCheckValidCallBack = VnPayUtil.isValidVnPayCallback(request, vnPayConfig.getSecretKey());
        if(!isCheckValidCallBack){
            throw new ApplicationException("Giao dịch không hợp lệ");
        }

        String status = request.getParameter("vnp_ResponseCode");
        String transactionCode = request.getParameter("vnp_TxnRef");
        log.info("VNPay callback - txnRef: {}, responseCode: {}", transactionCode, status);

        if (status.equals("00")) {
            log.info("PAYMENT SUCCESSFULLY");
            this.subscriptionOrderService.handlePaymentSuccess(transactionCode);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "http://localhost:5173/payment-success?transactionCode=" + transactionCode)
                    .build();

        } else {
            log.info("PAYMENT UNSUCCESSFULLY");
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "http://localhost:5173/payment-failure?transactionCode=" + transactionCode)
                    .build();
        }

    }

    @GetMapping("/subscription-orders/transaction-code/{transactionCode}")
    public ResponseEntity<SubscriptionOrderResponseDTO> getSubscriptionOrderDetail(@PathVariable("transactionCode") String transactionCode) {

        return ResponseEntity.status(HttpStatus.OK).body(this.subscriptionOrderService.getSubscriptionOrderDetailByTransactionCode(transactionCode));
    }

    @GetMapping("/subscription-orders/active-latest")
    public ResponseEntity<SubscriptionOrderResponseDTO> getActiveLatestSubscriptionOrder(@RequestParam long subscriptionPlanId) {

        return ResponseEntity.status(HttpStatus.OK).body(this.subscriptionOrderService.getActiveLatestSubscriptionOrderForUser(subscriptionPlanId));
    }

    @GetMapping("/subscription-orders")
    public ResponseEntity<ResultPaginationDTO> getSubscriptionOrders(@RequestParam(name = "size", defaultValue = "10") int size,
                                                                     @RequestParam(name = "page", defaultValue = "1") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(this.subscriptionOrderService.getSubscriptionOrders(size, page));
    }

}
