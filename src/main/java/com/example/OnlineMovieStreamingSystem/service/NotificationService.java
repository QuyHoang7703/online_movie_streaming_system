package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.notification.NotificationRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.notification.NotificationResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.notification.NotificationStatusResponseDTO;

import java.util.List;

public interface NotificationService {
    void sendNotificationToUser(String receiverId, NotificationRequestDTO notificationRequestDTO);
    void sendNotificationToUsers(List<Long> receiverIds, NotificationRequestDTO notificationRequestDTO);
    ResultPaginationDTO getNotificationsForUser(int page, int size);
    NotificationResponseDTO updateSeenStatusForNotification(long notificationId);
    List<NotificationStatusResponseDTO> updateSeenStatusForAllNotifications();
}
