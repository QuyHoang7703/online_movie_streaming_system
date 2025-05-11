package com.example.OnlineMovieStreamingSystem.dto.response.videoVersion;

import com.example.OnlineMovieStreamingSystem.util.constant.VideoType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoVersionResponseDTO {
    private long id;
    private VideoType videoType;
    private String videoUrl;
}
