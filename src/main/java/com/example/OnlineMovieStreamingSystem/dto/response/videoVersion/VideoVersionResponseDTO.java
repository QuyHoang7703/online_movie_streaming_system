package com.example.OnlineMovieStreamingSystem.dto.response.videoVersion;

import com.example.OnlineMovieStreamingSystem.util.constant.VideoType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoVersionResponseDTO {
    private long id;
    private long movieId;
    private VideoType videoType;
    private String movieTitle;
    private String backdropUrl;
}
