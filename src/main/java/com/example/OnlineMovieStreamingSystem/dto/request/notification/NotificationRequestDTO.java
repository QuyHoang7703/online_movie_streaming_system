package com.example.OnlineMovieStreamingSystem.dto.request.notification;

import com.example.OnlineMovieStreamingSystem.util.constant.NotificationType;
import com.example.OnlineMovieStreamingSystem.util.constant.TargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationRequestDTO {
    private String title;
    private String message;
    private NotificationType notificationType;
    private Long targetId;
    private TargetType targetType;

}
