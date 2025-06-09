package com.example.OnlineMovieStreamingSystem.dto.request.modelRecommendation;

import com.example.OnlineMovieStreamingSystem.dto.response.interaction.NeuMFFormatDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModelRetrain {
    private List<NeuMFFormatDTO> ratings;
    private Object movies;
}
