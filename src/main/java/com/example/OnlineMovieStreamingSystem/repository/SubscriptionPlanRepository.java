package com.example.OnlineMovieStreamingSystem.repository;

import com.example.OnlineMovieStreamingSystem.domain.SubscriptionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    Optional<SubscriptionPlan> findById(Long id);
    List<SubscriptionPlan> findByIdIn(List<Long> subscriptionIds);
    @Query("SELECT sp FROM SubscriptionPlan sp ")
    Page<SubscriptionPlan> findByFilters(Pageable pageable);

    @Query("SELECT sp FROM SubscriptionPlan sp " +
            "WHERE :subscriptionId IS NULL OR " +
            "sp.id != :subscriptionId")
    List<SubscriptionPlan> getParentOptions(Long subscriptionId);

    @Query("SELECT sp FROM SubscriptionPlan sp " +
            "WHERE sp.parentPlans IS EMPTY ")
    List<SubscriptionPlan> findRootPlans();




}
