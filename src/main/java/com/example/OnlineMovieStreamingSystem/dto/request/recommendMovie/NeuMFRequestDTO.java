package com.example.OnlineMovieStreamingSystem.dto.request.recommendMovie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NeuMFRequestDTO {
    private Long user_id;
}
