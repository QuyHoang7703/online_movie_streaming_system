package com.example.OnlineMovieStreamingSystem.client;


import com.example.OnlineMovieStreamingSystem.dto.request.modelRecommendation.ModelRetrain;
import com.example.OnlineMovieStreamingSystem.dto.response.modelRecomendation.RetrainModelResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="retran-neumf-model", url="http://localhost:5000")
public interface RetrainNeuMFClient {
    @PostMapping("/api/retrain/neumf")
    RetrainModelResponse retrainNeuMFModel(ModelRetrain modelRetrain);
}
