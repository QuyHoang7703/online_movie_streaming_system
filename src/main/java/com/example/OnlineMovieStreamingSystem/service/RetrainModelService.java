package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.request.modelRecommendation.ModelRetrain;
import com.example.OnlineMovieStreamingSystem.dto.response.modelRecomendation.RetrainModelResponse;

public interface RetrainModelService {
    ModelRetrain retrainNeuMFModel();
    RetrainModelResponse retrainCBFModel();
}
