package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.PlanDuration;
import com.example.OnlineMovieStreamingSystem.domain.SubscriptionPlan;
import com.example.OnlineMovieStreamingSystem.dto.request.subscriptionPlan.SubscriptionPlanRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.PlanDurationResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.SubscriptionPlanResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.SubscriptionPlanRepository;
import com.example.OnlineMovieStreamingSystem.service.SubscriptionService;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Override
    public SubscriptionPlanResponseDTO createSubscriptionPlan(SubscriptionPlanRequestDTO subscriptionPlanRequestDTO) {
        SubscriptionPlan subscriptionPlan = new SubscriptionPlan();
        subscriptionPlan.setName(subscriptionPlanRequestDTO.getName());

        String descriptions = String.join("!", subscriptionPlanRequestDTO.getDescription());
        subscriptionPlan.setDescription(descriptions);

        if(subscriptionPlanRequestDTO.getPlanDurations() != null) {
            List<PlanDuration> planDurations = subscriptionPlanRequestDTO.getPlanDurations().stream()
                    .map(planDurationRequestDTO -> {
                        PlanDuration planDuration = new PlanDuration();
                        planDuration.setName(planDurationRequestDTO.getName());
                        planDuration.setPrice(planDurationRequestDTO.getPrice());
                        planDuration.setDurationInMonths(planDurationRequestDTO.getDurationInMonths());
                        planDuration.setSubscriptionPlan(subscriptionPlan);
                        return planDuration;
                    }).toList();
            subscriptionPlan.setPlanDurations(planDurations);
        }

        if(subscriptionPlanRequestDTO.getParentId() != null) {
            SubscriptionPlan parentSubscriptionPlan = this.subscriptionPlanRepository.findById(subscriptionPlanRequestDTO.getParentId())
                    .orElseThrow(() -> new ApplicationException("Parent subscription plan not found"));
            subscriptionPlan.setParentSubscriptionPlan(parentSubscriptionPlan);
        }

        this.subscriptionPlanRepository.save(subscriptionPlan);
        return this.convertToSubscriptionPlanResponseDTO(subscriptionPlan);
    }

    @Override
    public SubscriptionPlanResponseDTO getSubscriptionPlan(long subscriptionPlanId) {
        SubscriptionPlan subscriptionPlan = this.subscriptionPlanRepository.findById(subscriptionPlanId)
                .orElseThrow(() -> new ApplicationException("Subscription plan not found"));

        return this.convertToSubscriptionPlanResponseDTO(subscriptionPlan);
    }

    private SubscriptionPlanResponseDTO convertToSubscriptionPlanResponseDTO(SubscriptionPlan subscriptionPlan) {
        SubscriptionPlanResponseDTO subscriptionPlanResponseDTO = new SubscriptionPlanResponseDTO();
        subscriptionPlanResponseDTO.setId(subscriptionPlan.getId());
        subscriptionPlanResponseDTO.setName(subscriptionPlan.getName());

        List<String> descriptions = Arrays.asList(subscriptionPlan.getDescription().split("!"));
        subscriptionPlanResponseDTO.setDescription(descriptions);

        if(subscriptionPlan.getPlanDurations() != null) {
            List<PlanDuration> planDurations = subscriptionPlan.getPlanDurations();
            List<PlanDurationResponseDTO> planDurationResponseDTOS = planDurations.stream()
                    .map(this::convertToPlanDurationResponseDTO)
                    .toList();
            subscriptionPlanResponseDTO.setPlanDurations(planDurationResponseDTOS);
        }

        return subscriptionPlanResponseDTO;
    }

    private PlanDurationResponseDTO convertToPlanDurationResponseDTO(PlanDuration planDuration) {
        PlanDurationResponseDTO planDurationResponseDTO = PlanDurationResponseDTO.builder()
                .id(planDuration.getId())
                .name(planDuration.getName())
                .price(planDuration.getPrice())
                .durationInMonths(planDuration.getDurationInMonths())
                .build();

        return planDurationResponseDTO;
    }
}
