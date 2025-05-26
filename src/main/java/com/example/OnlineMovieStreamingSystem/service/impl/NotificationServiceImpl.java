package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.Notification;
import com.example.OnlineMovieStreamingSystem.domain.UserNotification;
import com.example.OnlineMovieStreamingSystem.domain.user.User;
import com.example.OnlineMovieStreamingSystem.dto.Meta;
import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.notification.NotificationRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.notification.NotificationResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.notification.NotificationStatusResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.NotificationRepository;
import com.example.OnlineMovieStreamingSystem.repository.UserNotificationRepository;
import com.example.OnlineMovieStreamingSystem.repository.UserRepository;
import com.example.OnlineMovieStreamingSystem.service.NotificationService;
import com.example.OnlineMovieStreamingSystem.util.SecurityUtil;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;

    @Transactional
    @Override
    public void sendNotificationToUser(String receiverId, NotificationRequestDTO notificationRequestDTO) {
        messagingTemplate.convertAndSendToUser(receiverId, "/notifications", notificationRequestDTO);
        User receiver = this.userRepository.findByEmail(receiverId)
                .orElseThrow(() -> new ApplicationException("Không tồn tại user với id là " + receiverId));

        Notification notification = this.createNotification(notificationRequestDTO);

        UserNotification userNotification = new UserNotification();
        userNotification.setSeen(false);

        userNotification.setNotification(notification);
        userNotification.setUser(receiver);
        notification.setUserNotifications(Collections.singletonList(userNotification));

        this.notificationRepository.save(notification);
        log.info("Sended notification to " + receiver.getEmail());
    }

    @Transactional
    @Override
    public void sendNotificationToUsers(List<String> receiverIds, NotificationRequestDTO notificationRequestDTO) {
        Notification notification = this.createNotification(notificationRequestDTO);
        List<UserNotification> userNotifications = new ArrayList<>();
        List<User> receivers = this.userRepository.findAllById(receiverIds);
        for(User receiver : receivers) {
            UserNotification un = new UserNotification();
            un.setUser(receiver);
            un.setSeen(false);
            un.setNotification(notification);
            userNotifications.add(un);

            messagingTemplate.convertAndSendToUser(receiver.getEmail(), "/notifications", notificationRequestDTO);
            log.info("Sended notification to admins: " + receiver.getEmail());
        }

        notification.setUserNotifications(userNotifications);
        notificationRepository.save(notification); // cascade sẽ lưu hết
    }

    @Override
    public ResultPaginationDTO getNotificationsForUser(int page, int size) {
        String email = SecurityUtil.getLoggedEmail();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.Direction.DESC, "notification.createdAt");

        Page<UserNotification> userNotificationPage = this.userNotificationRepository.findAllByUserEmail(email, pageable);

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(userNotificationPage.getTotalPages());
        meta.setTotalElements(userNotificationPage.getTotalElements());

        List<NotificationResponseDTO> notificationResponseDTOS = userNotificationPage.getContent().stream()
                .map(this::convertToNotificationResponseDTO)
                .toList();

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(notificationResponseDTOS);

        return resultPaginationDTO;
    }

    @Override
    public NotificationResponseDTO updateSeenStatusForNotification(long notificationId) {
        String email = SecurityUtil.getLoggedEmail();
        UserNotification userNotification = this.userNotificationRepository.findByUserEmailAndId(email, notificationId)
                .orElseThrow(() -> new ApplicationException("Không tồn tại thông báo với id là " + notificationId + " của " + email));

        userNotification.setSeen(true);
        userNotification.setSeenTime(Instant.now());

        UserNotification updatedUserNotification = this.userNotificationRepository.save(userNotification);

        return this.convertToNotificationResponseDTO(updatedUserNotification);

    }

    @Override
    public List<NotificationStatusResponseDTO> updateSeenStatusForAllNotifications() {
        String email = SecurityUtil.getLoggedEmail();
        List<UserNotification> userNotifications = this.userNotificationRepository.findByUserEmailAndSeen(email, false);
        // Cập nhật trạng thái đã đọc
        userNotifications.forEach(userNotification -> {
            userNotification.setSeen(true);
            userNotification.setSeenTime(Instant.now());
        });

        // Lưu lại các thay đổi vào DB
        this.userNotificationRepository.saveAll(userNotifications);

        List<NotificationStatusResponseDTO> notificationStatusResponseDTOS = userNotifications.stream()
                .map(this::convertToNotificationStatusResponseDTO)
                .toList();

        return notificationStatusResponseDTOS;
    }


    private Notification createNotification(NotificationRequestDTO notificationRequestDTO) {
        Notification notification = new Notification();
        notification.setTitle(notificationRequestDTO.getTitle());
        notification.setMessage(notificationRequestDTO.getMessage());
        notification.setNotificationType(notificationRequestDTO.getNotificationType());
        notification.setTargetId(notificationRequestDTO.getTargetId());
        notification.setTargetType(notificationRequestDTO.getTargetType());

        return notification;

    }

    private NotificationResponseDTO convertToNotificationResponseDTO(UserNotification userNotification) {
        Notification notification = userNotification.getNotification();
        NotificationResponseDTO notificationResponseDTO = NotificationResponseDTO.builder()
                .id(userNotification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType())
                .targetId(notification.getTargetId())
                .targetType(notification.getTargetType())
                .seen(userNotification.isSeen())
                .seenTime(userNotification.getSeenTime())
                .createdAt(notification.getCreatedAt())
                .build();

        return notificationResponseDTO;
    }

    private NotificationStatusResponseDTO convertToNotificationStatusResponseDTO(UserNotification userNotification) {
        NotificationStatusResponseDTO notificationStatusResponseDTO = NotificationStatusResponseDTO.builder()
                .id(userNotification.getId())
                .seen(userNotification.isSeen())
                .build();

        return notificationStatusResponseDTO;
    }

}
