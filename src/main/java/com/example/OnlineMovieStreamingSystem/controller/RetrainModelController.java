package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.request.modelRecommendation.ModelRetrain;
import com.example.OnlineMovieStreamingSystem.service.RetrainModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/retrain-model/")
@RequiredArgsConstructor
public class RetrainModelController {
    private final RetrainModelService retrainModelService;

    @GetMapping("/neumf")
    public ResponseEntity<ModelRetrain> retrainModelNeuMF() {

        return ResponseEntity.status(HttpStatus.OK).body(this.retrainModelService.retrainNeuMFModel());
    }
}
