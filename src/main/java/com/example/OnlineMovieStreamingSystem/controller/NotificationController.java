package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.notification.NotificationResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.notification.NotificationStatusResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.NotificationService;
import com.example.OnlineMovieStreamingSystem.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    @GetMapping("/me")
    public ResponseEntity<ResultPaginationDTO> getNotificationsForUser(@RequestParam(name = "page", defaultValue = "1") int page,
                                                                       @RequestParam(name = "size", defaultValue = "5") int size) {

        return ResponseEntity.status(HttpStatus.OK).body(this.notificationService.getNotificationsForUser(page, size));
    }

    @PatchMapping("{notificationId}")
    @ApiMessage("Đánh dấu thông báo đã xem thành công")
    public ResponseEntity<NotificationResponseDTO> updateSeenStatusForNotification(@PathVariable("notificationId") long notificationId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.notificationService.updateSeenStatusForNotification(notificationId));
    }

    @PatchMapping("/mark-all-seen")
    @ApiMessage("Đánh dấu tất cả thông báo đã xem")
    public ResponseEntity<List<NotificationStatusResponseDTO>> updateSeenStatusForNotification() {
        return ResponseEntity.status(HttpStatus.OK).body(this.notificationService.updateSeenStatusForAllNotifications());
    }




}
