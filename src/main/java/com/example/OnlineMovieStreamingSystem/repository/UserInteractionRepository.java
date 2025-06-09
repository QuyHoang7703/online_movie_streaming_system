package com.example.OnlineMovieStreamingSystem.repository;

import com.example.OnlineMovieStreamingSystem.domain.UserInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {
    @Query("SELECT COUNT(ui) FROM UserInteraction ui " +
            "WHERE ui.userTemporaryId = :userId")
    long countRatingsOfUser(@Param("userId") long userId);

}
