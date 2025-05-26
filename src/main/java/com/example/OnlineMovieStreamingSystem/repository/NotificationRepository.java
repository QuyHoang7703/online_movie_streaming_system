package com.example.OnlineMovieStreamingSystem.repository;

import com.example.OnlineMovieStreamingSystem.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n " +
            "JOIN n.userNotifications un " +
            "JOIN un.user u " +
            "WHERE u.email = :email")
    Page<Notification> findByEmail(String email, Pageable pageable);
}
