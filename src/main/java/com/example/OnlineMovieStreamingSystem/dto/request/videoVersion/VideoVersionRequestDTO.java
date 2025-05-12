package com.example.OnlineMovieStreamingSystem.dto.request.videoVersion;

import com.example.OnlineMovieStreamingSystem.util.constant.VideoType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoVersionRequestDTO {
    @Enumerated(EnumType.STRING)
    private VideoType videoType;
}
