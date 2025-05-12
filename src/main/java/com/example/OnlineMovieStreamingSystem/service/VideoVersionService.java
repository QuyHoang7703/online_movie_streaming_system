package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.domain.VideoVersion;
import com.example.OnlineMovieStreamingSystem.dto.request.videoVersion.VideoVersionRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.videoVersion.VideoVersionResponseDTO;
import com.example.OnlineMovieStreamingSystem.util.constant.VideoType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public interface VideoVersionService {
//    String resolveVideoUrl(String videoUrl, MultipartFile video) throws IOException;
//    void addVideoVersionIfAvailable(List<VideoVersion> videoVersions, String videoUrl, VideoType videoType, Consumer<VideoVersion> ownerSetter);
    VideoVersionResponseDTO convertToVideoVersionResponseDTO(VideoVersion videoVersion);
    VideoVersionResponseDTO createVideoVersion(long videoVersionId, VideoVersionRequestDTO videoVersionRequestDTO);
    VideoVersionResponseDTO updateVideoVersion(long videoVersionId, VideoVersionRequestDTO videoVersionRequestDTO);
    void deleteVideoVersion(long videoVersionId);
}
