package com.example.OnlineMovieStreamingSystem.repository;

import com.example.OnlineMovieStreamingSystem.domain.UserNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    Page<UserNotification> findAllByUserEmail(String email, Pageable pageable);
    Optional<UserNotification> findByUserEmailAndId(String userEmail, long notificationId);
    List<UserNotification> findByUserEmailAndSeen(String userEmail, boolean seen);
}
