package com.example.OnlineMovieStreamingSystem.repository;

import com.example.OnlineMovieStreamingSystem.domain.SubscriptionOrder;
import com.example.OnlineMovieStreamingSystem.domain.SubscriptionPlan;
import com.example.OnlineMovieStreamingSystem.util.constant.SubscriptionOrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionOrderRepository extends JpaRepository<SubscriptionOrder, Long> {
    boolean existsByUserIdAndPlanDuration_IdInAndStatusAndEndDateAfter(
            long userId,
            List<Long> planDurationIds,
            SubscriptionOrderStatus status,
            LocalDate date
    );

    Optional<SubscriptionOrder> findByTransactionCode(String transactionCode);


    @Query("SELECT so FROM SubscriptionOrder so " +
            "WHERE so.user.id = :userId " +
            "AND so.planDuration.subscriptionPlan.id = :subscriptionPlanId " +
            "AND so.status = :status " +
            "ORDER BY so.endDate DESC")
    List<SubscriptionOrder> findLatestActiveOrderByUserAndSubscriptionPlan(
            @Param("userId") Long userId,
            @Param("subscriptionPlanId") long subscriptionPlanId,
            @Param("status") SubscriptionOrderStatus status,
            Pageable pageable);


    @Query("""
        SELECT so FROM SubscriptionOrder so
        WHERE so.user.id = :userId
          AND so.planDuration.subscriptionPlan.id IN :subscriptionPlanIds
          AND so.status = :status
          AND so.endDate <= :parentOrderEndDate
    """)
    List<SubscriptionOrder> findByUserIdAndSubscriptionPlanIdsAndStatus(@Param("userId") long userId,
                                                                        @Param("subscriptionPlanIds") List<Long> subscriptionPlanIds,
                                                                        @Param("status") SubscriptionOrderStatus status,
                                                                        @Param("parentOrderEndDate") LocalDate parentOrderEndDate);

}
