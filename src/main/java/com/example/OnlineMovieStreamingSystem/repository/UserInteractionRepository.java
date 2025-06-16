package com.example.OnlineMovieStreamingSystem.repository;

import com.example.OnlineMovieStreamingSystem.domain.UserInteraction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {
    @Query("SELECT COUNT(ui) FROM UserInteraction ui " +
            "WHERE ui.userTemporaryId = :userId")
    long countRatingsOfUser(@Param("userId") long userId);

    Optional<UserInteraction> findByUserIdAndMovieId(long userId, long movieId);

    @Query("SELECT ui FROM UserInteraction ui " +
            "WHERE ui.userTemporaryId = :userId")
    Page<UserInteraction> findByUserId(@Param("userId") Long userId, Pageable pageable);

}
