package com.example.OnlineMovieStreamingSystem.dto.response.videoVersion;

import com.example.OnlineMovieStreamingSystem.util.constant.VideoType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoVersionDetailResponseDTO {
    private long id;
    private VideoType videoType;
    private int episodeCount;
}
