package com.example.OnlineMovieStreamingSystem.dto.response.notification;

import com.example.OnlineMovieStreamingSystem.util.constant.NotificationType;
import com.example.OnlineMovieStreamingSystem.util.constant.TargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponseDTO {
    private long id;
    private String title;
    private String message;
    private NotificationType notificationType;
    private Long targetId;
    private TargetType targetType;
    private boolean seen;
    private Instant seenTime;
    private Instant createdAt;
}
