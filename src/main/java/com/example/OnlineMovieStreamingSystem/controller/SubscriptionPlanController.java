package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.subscriptionPlan.SubscriptionPlanRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.SubscriptionPlanResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.SubscriptionPlanSummaryDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.SubscriptionPlanTreeDTO;
import com.example.OnlineMovieStreamingSystem.service.SubscriptionService;
import com.example.OnlineMovieStreamingSystem.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/subscription-plans")
@Slf4j
public class SubscriptionPlanController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    @ApiMessage("Thêm gói thành công")
    public ResponseEntity<SubscriptionPlanResponseDTO> createSubscriptionPlan(@RequestBody SubscriptionPlanRequestDTO subscriptionPlanRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionService.createSubscriptionPlan(subscriptionPlanRequestDTO));
    }

    @GetMapping("{subscriptionPlanId}")
    public ResponseEntity<SubscriptionPlanResponseDTO> getSubscriptionPlanById(@PathVariable("subscriptionPlanId") long subscriptionPlanId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.subscriptionService.getSubscriptionPlanById(subscriptionPlanId));
    }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getSubscriptionPlans (@RequestParam(name="page", defaultValue = "1") int page,
                                                                     @RequestParam(name="size", defaultValue = "3") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(this.subscriptionService.getSubscriptionPlans(page, size));
    }

    @GetMapping("/options")
    public ResponseEntity<List<SubscriptionPlanSummaryDTO>> getSubscriptionPlansOptions(@RequestParam(required = false) Long currentSubscriptionPlanId) {

        return ResponseEntity.status(HttpStatus.OK).body(this.subscriptionService.getSubscriptionPlanOptions(currentSubscriptionPlanId));
    }

    @PatchMapping("{subscriptionPlanId}")
    @ApiMessage("Cập nhập gói dịch vụ thành công")
    public ResponseEntity<SubscriptionPlanResponseDTO> updateSubscriptionPlan(@PathVariable("subscriptionPlanId") long subscriptionPlanId,
                                                                              @RequestBody SubscriptionPlanRequestDTO subscriptionPlanRequestDTO) {

        return ResponseEntity.status(HttpStatus.OK).body(this.subscriptionService.updateSubscriptionPlan(subscriptionPlanId, subscriptionPlanRequestDTO));
    }

    @DeleteMapping("{subscriptionPlanId}")
    @ApiMessage("Xóa gói dịch vụ thành công")
    public ResponseEntity<Void> deleteSubscriptionPlan(@PathVariable("subscriptionPlanId") long subscriptionPlanId) {
        this.subscriptionService.deleteSubscriptionPlan(subscriptionPlanId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }



}
