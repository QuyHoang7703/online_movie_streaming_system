package com.example.OnlineMovieStreamingSystem.dto.request.episode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EpisodeRequestDTO {
    private String title;
    private int episodeNumber;
    private String videoUrl; // null khi chọn chức năng upload file
    private int duration;
}
