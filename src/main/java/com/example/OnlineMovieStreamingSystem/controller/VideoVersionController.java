package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.request.videoVersion.VideoVersionRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.videoVersion.VideoVersionResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.VideoVersionService;
import com.example.OnlineMovieStreamingSystem.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/movies")
public class VideoVersionController {
    private final VideoVersionService videoVersionService;

    @PostMapping("/{movieId}/video-versions")
    @ApiMessage("Đã thêm thành công phiên bản video cho phim")
    public ResponseEntity<VideoVersionResponseDTO> createVideoVersion(@PathVariable("movieId") long movieId, @RequestBody VideoVersionRequestDTO videoVersionRequestDTO) {

        return ResponseEntity.ok(videoVersionService.createVideoVersion(movieId, videoVersionRequestDTO));
    }

    @DeleteMapping("/video-versions/{videoVersionId}")
    @ApiMessage("Đã xóa thành công phiên bản video của phim")
    public ResponseEntity<Void> deleteVideoVersion(@PathVariable Long videoVersionId) {
        videoVersionService.deleteVideoVersion(videoVersionId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
