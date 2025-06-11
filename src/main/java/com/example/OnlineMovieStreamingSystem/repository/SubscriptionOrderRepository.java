package com.example.OnlineMovieStreamingSystem.repository;

import com.example.OnlineMovieStreamingSystem.domain.SubscriptionOrder;
import com.example.OnlineMovieStreamingSystem.domain.SubscriptionPlan;
import com.example.OnlineMovieStreamingSystem.dto.response.statistic.MonthlyRevenueResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.statistic.YearRevenueDTO;
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

    @Query("SELECT new com.example.OnlineMovieStreamingSystem.dto.response.statistic.MonthlyRevenueResponseDTO(" +
            "EXTRACT(MONTH FROM so.createAt), SUM(so.price)) " + // Sử dụng EXTRACT(MONTH FROM ...)
            "FROM SubscriptionOrder so " +
            "WHERE EXTRACT(YEAR FROM so.createAt) = :year " +   // Sử dụng EXTRACT(YEAR FROM ...)
            "GROUP BY EXTRACT(MONTH FROM so.createAt) " +
            "ORDER BY EXTRACT(MONTH FROM so.createAt)")         // Nên thêm ORDER BY để sắp xếp kết quả
    List<MonthlyRevenueResponseDTO> getMonthlyRevenue(@Param("year") int year);

    @Query("SELECT SUM(so.price) FROM SubscriptionOrder so " +
            "WHERE FUNCTION('MONTH', so.createAt) = :month " +
            "AND FUNCTION('YEAR', so.createAt) = :year")
    Double getRevenueByMonth(@Param("month") int month, @Param("year") int year);


    @Query("SELECT new com.example.OnlineMovieStreamingSystem.dto.response.statistic.YearRevenueDTO(" +
            "EXTRACT(YEAR FROM so.createAt), SUM(so.price)) " + // SELECT năm và tổng tiền
            "FROM SubscriptionOrder so " +
            "WHERE EXTRACT(YEAR FROM so.createAt) BETWEEN :startYear AND :endYear " + // Bao gồm cả năm bắt đầu và kết thúc
            "GROUP BY EXTRACT(YEAR FROM so.createAt) " + // Nhóm theo NĂM
            "ORDER BY EXTRACT(YEAR FROM so.createAt) ASC") // Sắp xếp theo NĂM tăng dần
    List<YearRevenueDTO> getYearRevenue(@Param("startYear") int startYear, @Param("endYear") int endYear);



}
