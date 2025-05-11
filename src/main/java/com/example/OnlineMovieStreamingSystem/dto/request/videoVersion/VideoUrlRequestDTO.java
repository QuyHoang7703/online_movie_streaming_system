package com.example.OnlineMovieStreamingSystem.dto.request.videoVersion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoUrlRequestDTO {
    private String vietSubVideoUrl;
    private String dubbedVideoUrl;
    private String voiceOverVideoUrl;
}
