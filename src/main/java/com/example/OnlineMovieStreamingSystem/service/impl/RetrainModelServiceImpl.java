package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.client.RetrainNeuMFClient;
import com.example.OnlineMovieStreamingSystem.domain.UserInteraction;
import com.example.OnlineMovieStreamingSystem.dto.request.modelRecommendation.ModelRetrain;
import com.example.OnlineMovieStreamingSystem.dto.response.interaction.NeuMFFormatDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.modelRecomendation.RetrainModelResponse;
import com.example.OnlineMovieStreamingSystem.repository.UserInteractionRepository;
import com.example.OnlineMovieStreamingSystem.service.RetrainModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RetrainModelServiceImpl implements RetrainModelService {
    private final UserInteractionRepository userInteractionRepository;
    private final RetrainNeuMFClient retrainNeuMFClient;

    @Override
//    @Scheduled(cron = "0 0 2 * * ?")
    public ModelRetrain retrainNeuMFModel() {
        log.info("Start retrain NeuMF model");
        List<UserInteraction> userInteractions = this.userInteractionRepository.findAll();
        List<NeuMFFormatDTO> neuMFFormatDTOS = userInteractions.stream()
                        .map(this::convertToNeuMFFormatDTO).toList();
        ModelRetrain modelRetrain = new ModelRetrain();
        modelRetrain.setRatings(neuMFFormatDTOS);
//        RetrainModelResponse retrainModelResponse = this.retrainNeuMFClient.retrainNeuMFModel(modelRetrainRequest);

        log.info("Finish retrain NeuMF model");

        return modelRetrain;
    }

    @Override
    @Scheduled(cron = "0 0 2 * * ?")
    public RetrainModelResponse retrainCBFModel() {
        return null;
    }

    private NeuMFFormatDTO convertToNeuMFFormatDTO(UserInteraction userInteraction) {
        NeuMFFormatDTO formatDTO = NeuMFFormatDTO.builder()
                .userId(userInteraction.getUserTemporaryId())
                .movieId(userInteraction.getMovieTemporaryId())
                .timestamp(userInteraction.getUpdatedAt())
                .rating(userInteraction.getRating())
                .build();
        return formatDTO;
    }
}
