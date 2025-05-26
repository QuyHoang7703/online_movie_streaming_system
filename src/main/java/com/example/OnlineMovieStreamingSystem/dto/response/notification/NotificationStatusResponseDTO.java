package com.example.OnlineMovieStreamingSystem.dto.response.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationStatusResponseDTO {
    private long id;
    private boolean seen;
}
