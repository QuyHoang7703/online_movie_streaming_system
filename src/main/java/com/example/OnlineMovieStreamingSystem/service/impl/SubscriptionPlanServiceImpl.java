package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.domain.PlanDuration;
import com.example.OnlineMovieStreamingSystem.domain.SubscriptionPlan;
import com.example.OnlineMovieStreamingSystem.dto.Meta;
import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.subscriptionPlan.PlanDurationRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.subscriptionPlan.SubscriptionPlanRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.PlanDurationResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.SubscriptionPlanResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.SubscriptionPlanSummaryDTO;
import com.example.OnlineMovieStreamingSystem.repository.SubscriptionPlanRepository;
import com.example.OnlineMovieStreamingSystem.service.SubscriptionPlanService;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Override
    public SubscriptionPlanResponseDTO createSubscriptionPlan(SubscriptionPlanRequestDTO subscriptionPlanRequestDTO) {
        SubscriptionPlan subscriptionPlan = new SubscriptionPlan();
        subscriptionPlan.setName(subscriptionPlanRequestDTO.getName());
        subscriptionPlan.setDescription(subscriptionPlanRequestDTO.getDescription());
        subscriptionPlan.setActive(subscriptionPlanRequestDTO.isActive());

        String features = String.join("!", subscriptionPlanRequestDTO.getFeatures());
        subscriptionPlan.setFeatures(features);

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

        if(subscriptionPlanRequestDTO.getParentPlanIds() != null && !subscriptionPlanRequestDTO.getParentPlanIds().isEmpty()) {
            List<SubscriptionPlan> parentPlans = this.subscriptionPlanRepository.findByIdIn(subscriptionPlanRequestDTO.getParentPlanIds());
            if (parentPlans == null || parentPlans.isEmpty()) {
                throw new ApplicationException("Gói cha không tồn tại");
            }
            subscriptionPlan.setParentPlans(parentPlans);
        }


        this.subscriptionPlanRepository.save(subscriptionPlan);
        return this.convertToSubscriptionPlanResponseDTO(subscriptionPlan);
    }

    @Override
    public SubscriptionPlanResponseDTO getSubscriptionPlanById(long subscriptionPlanId) {
        SubscriptionPlan subscriptionPlan = this.subscriptionPlanRepository.findById(subscriptionPlanId)
                .orElseThrow(() -> new ApplicationException("Subscription plan not found"));

        return this.convertToSubscriptionPlanResponseDTO(subscriptionPlan);
    }

    @Override
    public ResultPaginationDTO getSubscriptionPlans(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);

        Page<SubscriptionPlan> subscriptionPlanPage = this.subscriptionPlanRepository.findAll(pageable);

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();

        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber() +1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(subscriptionPlanPage.getTotalPages());
        meta.setTotalElements(subscriptionPlanPage.getTotalElements());

        resultPaginationDTO.setMeta(meta);

        List<SubscriptionPlanResponseDTO> subscriptionPlanResponseDTOS = subscriptionPlanPage.getContent().stream()
                .map(this::convertToSubscriptionPlanResponseDTO)
                .toList();

        resultPaginationDTO.setResult(subscriptionPlanResponseDTOS);

        return resultPaginationDTO;
    }

    @Override
    public List<SubscriptionPlanSummaryDTO> getSubscriptionPlanOptions(Long subscriptionPlanId) {
        List<SubscriptionPlan> subscriptionPlans = this.subscriptionPlanRepository.getParentOptions(subscriptionPlanId);
        List<SubscriptionPlanSummaryDTO> subscriptionPlanSummaryDTOS = subscriptionPlans.stream()
                .map(this::convertToSubscriptionPlanSummaryDTO)
                .toList();
        return subscriptionPlanSummaryDTOS;
    }

    @Override
    public SubscriptionPlanResponseDTO updateSubscriptionPlan(long subscriptionPlanId, SubscriptionPlanRequestDTO subscriptionPlanRequestDTO) {
        SubscriptionPlan subscriptionPlanDB = this.subscriptionPlanRepository.findById(subscriptionPlanId)
                .orElseThrow(() -> new ApplicationException("Gói dịch vụ không tồn tại"));
        if(!Objects.equals(subscriptionPlanRequestDTO.getName(), subscriptionPlanDB.getName())) {
            subscriptionPlanDB.setName(subscriptionPlanRequestDTO.getName());
        }
        if(!Objects.equals(subscriptionPlanRequestDTO.getDescription(), subscriptionPlanDB.getDescription())) {
            subscriptionPlanDB.setDescription(subscriptionPlanRequestDTO.getDescription());
        }
        if(!Objects.equals(subscriptionPlanRequestDTO.isActive(), subscriptionPlanDB.isActive())) {
            subscriptionPlanDB.setActive(subscriptionPlanRequestDTO.isActive());
        }

        if(subscriptionPlanRequestDTO.getFeatures() != null) {
            String features = String.join("!", subscriptionPlanRequestDTO.getFeatures());
            if(!Objects.equals(features, subscriptionPlanDB.getFeatures())) {
                subscriptionPlanDB.setFeatures(features);
            }
        }

        // Update parent plan
        Set<Long> currentParentPlanIds = subscriptionPlanDB.getParentPlans().stream()
                .map(SubscriptionPlan::getId).collect(Collectors.toSet());
        Set<Long> parentPlanRequestIds = new HashSet<>(subscriptionPlanRequestDTO.getParentPlanIds());
        if (parentPlanRequestIds.contains(subscriptionPlanId)) {
            throw new ApplicationException("Gói dịch vụ không thể là parent của chính nó");
        }
        if(!Objects.equals(currentParentPlanIds, parentPlanRequestIds)) {
            List<SubscriptionPlan> parentPlans = parentPlanRequestIds.stream()
                    .map(requestParentPlanId -> {
                        return this.subscriptionPlanRepository.findById(requestParentPlanId)
                                .orElseThrow(() -> new ApplicationException("Không tìm thấy gói dịch vụ"));
                    }).collect(Collectors.toList());
            subscriptionPlanDB.setParentPlans(parentPlans);
        }

        // Update plan duration
        List<PlanDuration> planDurations = subscriptionPlanDB.getPlanDurations();
        Map<Long, PlanDuration> planDurationMap = planDurations.stream()
                .collect(Collectors.toMap(PlanDuration::getId, planDuration -> planDuration));
        List<PlanDuration> updatePlanDurations = new ArrayList<>();
        for(PlanDurationRequestDTO planDurationRequestDTO : subscriptionPlanRequestDTO.getPlanDurations()) {
            PlanDuration planDuration = planDurationMap.get(planDurationRequestDTO.getId());
            if(planDuration == null) {
                planDuration = new PlanDuration();
                planDuration.setName(planDurationRequestDTO.getName());
                planDuration.setPrice(planDurationRequestDTO.getPrice());
                planDuration.setDurationInMonths(planDurationRequestDTO.getDurationInMonths());
                planDuration.setSubscriptionPlan(subscriptionPlanDB);
            }else if(!isSamePlanDuration(planDuration, planDurationRequestDTO)) {
                planDuration.setName(planDurationRequestDTO.getName());
                planDuration.setPrice(planDurationRequestDTO.getPrice());
                planDuration.setDurationInMonths(planDurationRequestDTO.getDurationInMonths());
            }
            updatePlanDurations.add(planDuration);
        }

        if(!updatePlanDurations.isEmpty()) {
            subscriptionPlanDB.getPlanDurations().clear();
            subscriptionPlanDB.getPlanDurations().addAll(updatePlanDurations);
        }

        SubscriptionPlan updatedSubscriptionPlan = this.subscriptionPlanRepository.save(subscriptionPlanDB);


        return this.convertToSubscriptionPlanResponseDTO(updatedSubscriptionPlan);
    }

    private boolean isSamePlanDuration (PlanDuration planDuration, PlanDurationRequestDTO planDurationRequestDTO) {
        if(!Objects.equals(planDuration.getId(), planDurationRequestDTO.getId())) {
            return false;
        }
        if(!Objects.equals(planDuration.getName(), planDurationRequestDTO.getName())) {
            return false;
        }
        if(!Objects.equals(planDuration.getPrice(), planDurationRequestDTO.getPrice())) {
            return false;
        }
        if(!Objects.equals(planDuration.getDurationInMonths(), planDurationRequestDTO.getDurationInMonths())) {
            return false;
        }

        return true;
    }

    @Override
    public void deleteSubscriptionPlan(long subscriptionPlanId) {
        SubscriptionPlan subscriptionPlan = this.subscriptionPlanRepository.findById(subscriptionPlanId)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy gói dịch vụ"));
        if (!subscriptionPlan.getParentPlans().isEmpty() || !subscriptionPlan.getChildPlans().isEmpty()) {
            throw new ApplicationException("Không thể xóa gói dịch vụ vì còn liên kết với các gói khác.");
        }
        for(Movie movie: subscriptionPlan.getMovies()) {
            movie.getSubscriptionPlans().remove(subscriptionPlan);
        }

        this.subscriptionPlanRepository.delete(subscriptionPlan);
    }

    @Override
    public SubscriptionPlanSummaryDTO convertToSubscriptionPlanSummaryDTO(SubscriptionPlan subscriptionPlan) {
        SubscriptionPlanSummaryDTO subscriptionPlanSummaryDTO = SubscriptionPlanSummaryDTO.builder()
                .id(subscriptionPlan.getId())
                .name(subscriptionPlan.getName())
                .build();
        if(subscriptionPlan.getParentPlans() != null) {
            List<Long> parentIds = subscriptionPlan.getParentPlans().stream().map(SubscriptionPlan::getId).toList();
            subscriptionPlanSummaryDTO.setParentIds(parentIds);
        }
        return subscriptionPlanSummaryDTO;
    }

    @Override
    public SubscriptionPlanResponseDTO convertToSubscriptionPlanResponseDTO(SubscriptionPlan subscriptionPlan) {
        SubscriptionPlanResponseDTO subscriptionPlanResponseDTO = new SubscriptionPlanResponseDTO();
        subscriptionPlanResponseDTO.setId(subscriptionPlan.getId());
        subscriptionPlanResponseDTO.setName(subscriptionPlan.getName());
        subscriptionPlanResponseDTO.setDescription(subscriptionPlan.getDescription());
        subscriptionPlanResponseDTO.setActive(subscriptionPlan.isActive());

        Set<String> allFeatures = new LinkedHashSet<>(); // LinkedHashSet để giữ thứ tự nếu các đặc trưng gốc quan trọng



        if(subscriptionPlan.getParentPlans() != null) {
            List<SubscriptionPlanSummaryDTO> parentPlans = subscriptionPlan.getParentPlans().stream()
                    .map(this::convertToSubscriptionPlanSummaryDTO)
                    .toList();
            subscriptionPlanResponseDTO.setParentPlans(parentPlans);

        }

        if(subscriptionPlan.getChildPlans() != null) {
            List<SubscriptionPlan> childSubscriptionPlans = subscriptionPlan.getChildPlans();
            List<SubscriptionPlanSummaryDTO> childPlans = childSubscriptionPlans.stream()
                    .map(this::convertToSubscriptionPlanSummaryDTO)
                    .toList();
            subscriptionPlanResponseDTO.setChildPlans(childPlans);
            getAllFeaturesFromChild(allFeatures, childSubscriptionPlans);
        }

        if(subscriptionPlan.getPlanDurations() != null) {
            List<PlanDuration> planDurations = subscriptionPlan.getPlanDurations();
            List<PlanDurationResponseDTO> planDurationResponseDTOS = planDurations.stream()
                    .map(this::convertToPlanDurationResponseDTO)
                    .toList();
            subscriptionPlanResponseDTO.setPlanDurations(planDurationResponseDTOS);
        }

        // Thêm đặc trưng của gói cha vào Set
        if (subscriptionPlan.getFeatures() != null && !subscriptionPlan.getFeatures().isEmpty()) {
            allFeatures.addAll(Arrays.asList(subscriptionPlan.getFeatures().split("!")));
        }

        subscriptionPlanResponseDTO.setFeatures(new ArrayList<>(allFeatures));

        return subscriptionPlanResponseDTO;
    }

    @Override
    public List<SubscriptionPlanSummaryDTO> getSubscriptionPlansForFilters() {
        List<SubscriptionPlan> subscriptionPlans = subscriptionPlanRepository.findAll();
        List<SubscriptionPlanSummaryDTO> subscriptionPlanSummaryDTOS = subscriptionPlans.stream()
                .map(this::convertToSubscriptionPlanSummaryDTO).toList();

        return subscriptionPlanSummaryDTOS;
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

    private void getAllFeaturesFromChild(Set<String> features,  List<SubscriptionPlan> childSubscriptionPlans) {
        for(SubscriptionPlan subscriptionPlan: childSubscriptionPlans) {
            features.addAll(Arrays.asList(subscriptionPlan.getFeatures().split("!")));
            if(subscriptionPlan.getChildPlans() != null && !subscriptionPlan.getChildPlans().isEmpty()) {
                getAllFeaturesFromChild(features, subscriptionPlan.getChildPlans());
            }
        }

    }


}
