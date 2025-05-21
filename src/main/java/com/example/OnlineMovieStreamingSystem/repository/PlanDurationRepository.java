package com.example.OnlineMovieStreamingSystem.repository;

import com.example.OnlineMovieStreamingSystem.domain.PlanDuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanDurationRepository extends JpaRepository<PlanDuration, Long> {
    List<PlanDuration> findByIdIn(List<Long> planDurationIds);

}
