package com.example.OnlineMovieStreamingSystem.dto.response.episode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EpisodeDetailResponseDTO {
    private long id;
    private String title;
    private int episodeNumber;
    private int duration;
    private String videoUrl;
    private long seriesMovieId;

}
