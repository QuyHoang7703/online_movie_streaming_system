package com.example.OnlineMovieStreamingSystem.dto.response.episode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EpisodeSummaryResponseDTO {
    private long id;
    private int episodeNumber;
    private String title;
    private int duration;
}
