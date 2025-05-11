package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.VideoVersion;
import com.example.OnlineMovieStreamingSystem.service.ImageStorageService;
import com.example.OnlineMovieStreamingSystem.service.VideoVersionService;
import com.example.OnlineMovieStreamingSystem.util.constant.VideoType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class VideoVersionServiceImpl implements VideoVersionService {
    private final ImageStorageService imageStorageService;
    private final String CONTAINER_NAME = "movie-video-container";
    @Override
    public String resolveVideoUrl(String videoUrl, MultipartFile video) throws IOException {
        if((videoUrl == null || videoUrl.isEmpty()) && video != null && !video.isEmpty()){
            return this.imageStorageService.uploadFile(CONTAINER_NAME, video.getOriginalFilename(), video.getInputStream());
        }
        return videoUrl;
    }

    @Override
    public void addVideoVersionIfAvailable(List<VideoVersion> videoVersions,
                                           String videoUrl,
                                           VideoType videoType,
                                           Consumer<VideoVersion> ownerSetter) {
      if(videoUrl != null && !videoUrl.isEmpty()){
          VideoVersion videoVersion = new VideoVersion();
          videoVersion.setVideoUrl(videoUrl);
          videoVersion.setVideoType(videoType);
          ownerSetter.accept(videoVersion);
          videoVersions.add(videoVersion);
      }
    }
}
