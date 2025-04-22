package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.request.subscriptionPlan.SubscriptionPlanRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.SubscriptionPlanResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/subscription-plans")
@Slf4j
public class SubscriptionPlanController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionPlanResponseDTO> createSubscriptionPlan(@RequestBody SubscriptionPlanRequestDTO subscriptionPlanRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionService.createSubscriptionPlan(subscriptionPlanRequestDTO));
    }

    @GetMapping("{subscriptionPlanId}")
    public ResponseEntity<SubscriptionPlanResponseDTO> getAllSubscriptionPlans(@PathVariable("subscriptionPlanId") long subscriptionPlanId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.subscriptionService.getSubscriptionPlan(subscriptionPlanId));
    }

}
