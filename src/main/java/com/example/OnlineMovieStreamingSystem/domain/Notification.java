package com.example.OnlineMovieStreamingSystem.domain;

import com.example.OnlineMovieStreamingSystem.domain.user.User;
import com.example.OnlineMovieStreamingSystem.util.constant.NotificationType;
import com.example.OnlineMovieStreamingSystem.util.constant.TargetType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private String message;
    private Instant createdAt;
    private NotificationType notificationType;

    private Long targetId;
    @Enumerated(EnumType.STRING)
    private TargetType targetType;


    @OneToMany(mappedBy = "notification")
    private List<UserNotification> userNotifications;

}
