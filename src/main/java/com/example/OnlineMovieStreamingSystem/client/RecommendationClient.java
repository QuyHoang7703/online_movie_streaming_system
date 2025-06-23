package com.example.OnlineMovieStreamingSystem.client;

import com.example.OnlineMovieStreamingSystem.dto.request.recommendMovie.NeuMFRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.recommendMovie.RecommendationMovieRequest;
import com.example.OnlineMovieStreamingSystem.dto.response.recommendMovie.RecommendationResponseWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="get-recommendation-movie", url="http://flask-recommendation:5001")
public interface RecommendationClient {
    @PostMapping("/api/recommend/hybrid")
    RecommendationResponseWrapper getRecommendationHybridResponse(RecommendationMovieRequest request);
    @PostMapping("/api/recommend")
    RecommendationResponseWrapper getRecommendationCBFResponse(RecommendationMovieRequest request);
    @PostMapping("/api/recommend/neumf/user")
    RecommendationResponseWrapper getRecommendationNeuMFResponse(NeuMFRequestDTO neuMFRequestDTO);
}
