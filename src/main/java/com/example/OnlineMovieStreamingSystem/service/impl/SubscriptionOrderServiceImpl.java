package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.config.VnPayConfig;
import com.example.OnlineMovieStreamingSystem.domain.PlanDuration;
import com.example.OnlineMovieStreamingSystem.domain.SubscriptionOrder;
import com.example.OnlineMovieStreamingSystem.domain.SubscriptionPlan;
import com.example.OnlineMovieStreamingSystem.domain.user.User;
import com.example.OnlineMovieStreamingSystem.dto.request.subscriptionOrder.SubscriptionOrderRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionOrder.PaymentUrlResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionOrder.SubscriptionOrderResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.PlanDurationRepository;
import com.example.OnlineMovieStreamingSystem.repository.SubscriptionOrderRepository;
import com.example.OnlineMovieStreamingSystem.repository.UserRepository;
import com.example.OnlineMovieStreamingSystem.service.SubscriptionOrderService;
import com.example.OnlineMovieStreamingSystem.util.SecurityUtil;
import com.example.OnlineMovieStreamingSystem.util.VnPayUtil;
import com.example.OnlineMovieStreamingSystem.util.constant.SubscriptionOrderStatus;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SubscriptionOrderServiceImpl implements SubscriptionOrderService {
    private final String ORDER_PREFIX = "order:";
    private final RedisTemplate<String, String> redisTemplate;
    private final PlanDurationRepository planDurationRepository;
    private final SubscriptionOrderRepository subscriptionOrderRepository;
    private final UserRepository userRepository;
    private final VnPayConfig vnPayConfig;

    @Override
    public PaymentUrlResponseDTO getPaymentUrl(HttpServletRequest request, SubscriptionOrderRequestDTO subscriptionOrderRequestDTO) {
        String email = SecurityUtil.getLoggedEmail();

        Long planDurationId = subscriptionOrderRequestDTO.getPlanDurationId();
        PlanDuration planDuration = this.planDurationRepository.findById(planDurationId)
                .orElseThrow(() -> new ApplicationException("Không tồn tại gói dịch vụ với id là " + planDurationId));

        // Tạo paymentUrl
        PaymentUrlResponseDTO paymentUrlResponseDTO = this.createUrlRequestToVnPay(request, planDuration.getPrice(), planDuration.getId(), email);

        return paymentUrlResponseDTO;
    }

    @Transactional
    @Override
    public void handlePaymentSuccess(String transactionCode) {
        String orderKey = ORDER_PREFIX + transactionCode;

        HashOperations<String, String , String> hashOperations = redisTemplate.opsForHash();

        String email = hashOperations.get(orderKey, "email");
        String planDurationIdStr = hashOperations.get(orderKey, "planDurationId");

        if (email == null || planDurationIdStr == null) {
            throw new ApplicationException("Không tìm thấy thông tin đơn hàng trong Redis. Giao dịch đã hết hạn hoặc không hợp lệ.");
        }

        long planDurationId = Long.parseLong(planDurationIdStr);

        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("Không tồn tại user với email là " + email));



        PlanDuration planDuration = this.planDurationRepository.findById(planDurationId)
                .orElseThrow(() -> new ApplicationException("Không tồn tại gói dịch vụ với id là " + planDurationId));

        SubscriptionOrder subscriptionOrder = new SubscriptionOrder();
        subscriptionOrder.setUser(user);
        subscriptionOrder.setPlanDuration(planDuration);

        // Lấy ra subscription order của user còn hạn và endDate là lớn nhất, để tính startDate
        LocalDate startDate = null;
        Pageable pageable = PageRequest.of(0, 1);
        List<SubscriptionOrder> orders  = this.subscriptionOrderRepository.findLatestActiveOrderByUserAndSubscriptionPlan(user.getId(),
                planDuration.getSubscriptionPlan().getId(),
                SubscriptionOrderStatus.ACTIVE,
                pageable);
        Optional<SubscriptionOrder> latestOrderOpt = orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));

        if (latestOrderOpt.isPresent()) {
            SubscriptionOrder latestOrder = latestOrderOpt.get();
            // Gói mới bắt đầu sau khi gói cũ kết thúc
            startDate = latestOrder.getEndDate().plusDays(1);
        }else{
            startDate = LocalDate.now();
        }


        subscriptionOrder.setStartDate(startDate);
        subscriptionOrder.setEndDate(startDate.plusMonths(planDuration.getDurationInMonths()).minusDays(1));
        subscriptionOrder.setStatus(SubscriptionOrderStatus.ACTIVE);
        subscriptionOrder.setPrice(planDuration.getPrice());
        subscriptionOrder.setTransactionCode(transactionCode);
        subscriptionOrder.setCreateAt(Instant.now());

        this.subscriptionOrderRepository.save(subscriptionOrder);

        // Expired child orders
        expiredChildrenSubscriptionOrder(planDurationId, user.getId(), subscriptionOrder.getEndDate());

        // Xóa redis key
        redisTemplate.delete(orderKey);
    }

    @Override
    public SubscriptionOrderResponseDTO getSubscriptionOrderDetailByTransactionCode(String transactionCode) {
        SubscriptionOrder subscriptionOrder = this.subscriptionOrderRepository.findByTransactionCode(transactionCode)
                .orElseThrow(() -> new ApplicationException("Không có hóa đơn nào với mã giao dịch là " + transactionCode));

        return this.convertToSubscriptionOrderResponseDTO(subscriptionOrder);
    }

    @Override
    public SubscriptionOrderResponseDTO getActiveLatestSubscriptionOrderForUser(long subscriptionPlanId) {
        String email = SecurityUtil.getLoggedEmail();
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("Không tồn tại user với email là " + email));
        Pageable pageable = PageRequest.of(0, 1);
        List<SubscriptionOrder> orders  = this.subscriptionOrderRepository.findLatestActiveOrderByUserAndSubscriptionPlan(user.getId(),
                subscriptionPlanId,
                SubscriptionOrderStatus.ACTIVE,
                pageable);
        Optional<SubscriptionOrder> latestOrderOpt = orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));
        return latestOrderOpt.map(this::convertToSubscriptionOrderResponseDTO).orElse(null);

    }

    private PaymentUrlResponseDTO createUrlRequestToVnPay (HttpServletRequest request, Double amount, long planDurationId, String email)  {
        // Create request payment with params to call VnPay's API
        String bankCode = request.getParameter("bankCode");
        // Get obligatory params
        Map<String, String> vnpParamsMap = vnPayConfig.getVnPayConfig();
        // Create additional params about transaction
        vnpParamsMap.put("vnp_Amount", String.valueOf(Math.round(amount* 100)));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VnPayUtil.getIpAddress(request));

        // Build request url
        String queryUrl = VnPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VnPayUtil.getPaymentURL(vnpParamsMap, false);
        queryUrl += "&vnp_SecureHash=" + VnPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;

        PaymentUrlResponseDTO res = new PaymentUrlResponseDTO();
        res.setMessage("Success");
        res.setPaymentUrl(paymentUrl);

        // Set order info in redis
        String transactionCode = vnpParamsMap.get("vnp_TxnRef");
        String orderKey = ORDER_PREFIX + transactionCode;
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(orderKey, "email", email);
        hashOperations.put(orderKey, "planDurationId", String.valueOf(planDurationId));
        redisTemplate.expire(orderKey, Duration.ofMinutes(5));


        return res;
    }

    private SubscriptionOrderResponseDTO convertToSubscriptionOrderResponseDTO(SubscriptionOrder subscriptionOrder) {
        SubscriptionOrderResponseDTO subscriptionOrderResponseDTO = SubscriptionOrderResponseDTO.builder()
                .id(subscriptionOrder.getId())
                .price(subscriptionOrder.getPrice())
                .startDate(subscriptionOrder.getStartDate())
                .endDate(subscriptionOrder.getEndDate())
                .transactionCode(subscriptionOrder.getTransactionCode())
                .createAt(subscriptionOrder.getCreateAt())
                .subscriptionPlanName(subscriptionOrder.getPlanDuration().getSubscriptionPlan().getName())
                .durationInMonths(subscriptionOrder.getPlanDuration().getDurationInMonths())
                .build();

        return subscriptionOrderResponseDTO;
    }


    private void expiredChildrenSubscriptionOrder(long parentPlanDurationId, String userId, LocalDate parentSubscriptionOrderEndDate) {
        PlanDuration parentPlanDuration = this.planDurationRepository.findById(parentPlanDurationId)
                .orElseThrow(() -> new ApplicationException("Không tồn tại gói dịch vụ với id là " + parentPlanDurationId));

        SubscriptionPlan subscriptionPlan = parentPlanDuration.getSubscriptionPlan();
        List<SubscriptionPlan> childrenSubscriptionPlan = subscriptionPlan.getChildPlans();
        if(childrenSubscriptionPlan == null || childrenSubscriptionPlan.isEmpty()) {
            return;
        }
        List<Long> childSubscriptionPlanIds = childrenSubscriptionPlan.stream().map(SubscriptionPlan::getId).toList();


        List<SubscriptionOrder> activeChildOrders  = this.subscriptionOrderRepository.findByUserIdAndSubscriptionPlanIdsAndStatus(userId,
                childSubscriptionPlanIds,
                SubscriptionOrderStatus.ACTIVE,
                parentSubscriptionOrderEndDate
        );

        if (activeChildOrders.isEmpty()) {
            return;
        }


        activeChildOrders.forEach(order -> order.setStatus(SubscriptionOrderStatus.EXPIRED));

        this.subscriptionOrderRepository.saveAll(activeChildOrders);


    }


}
