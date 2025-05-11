package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.domain.VideoVersion;
import com.example.OnlineMovieStreamingSystem.util.constant.VideoType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public interface VideoVersionService {
    String resolveVideoUrl(String videoUrl, MultipartFile video) throws IOException;
    void addVideoVersionIfAvailable(List<VideoVersion> videoVersions, String videoUrl, VideoType videoType, Consumer<VideoVersion> ownerSetter);
}
