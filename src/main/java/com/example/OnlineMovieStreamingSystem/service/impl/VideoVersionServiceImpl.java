package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.Episode;
import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.domain.VideoVersion;
import com.example.OnlineMovieStreamingSystem.dto.request.videoVersion.VideoVersionRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.videoVersion.VideoVersionDetailResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.videoVersion.VideoVersionResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.MovieRepository;
import com.example.OnlineMovieStreamingSystem.repository.VideoVersionRepository;
import com.example.OnlineMovieStreamingSystem.service.ImageStorageService;
import com.example.OnlineMovieStreamingSystem.service.VideoVersionService;
import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import com.example.OnlineMovieStreamingSystem.util.constant.VideoType;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class VideoVersionServiceImpl implements VideoVersionService {
    private final ImageStorageService imageStorageService;
    private final MovieRepository movieRepository;
    private final VideoVersionRepository videoVersionRepository;
    private final String CONTAINER_NAME = "movie-video-container";
//    @Override
//    public String resolveVideoUrl(String videoUrl, MultipartFile video) throws IOException {
//        if((videoUrl == null || videoUrl.isEmpty()) && video != null && !video.isEmpty()){
//            return this.imageStorageService.uploadFile(CONTAINER_NAME, video.getOriginalFilename(), video.getInputStream());
//        }
//        return videoUrl;
//    }
//
//    @Override
//    public void addVideoVersionIfAvailable(List<VideoVersion> videoVersions,
//                                           String videoUrl,
//                                           VideoType videoType,
//                                           Consumer<VideoVersion> ownerSetter) {
//      if(videoUrl != null && !videoUrl.isEmpty()){
//          VideoVersion videoVersion = new VideoVersion();
//          videoVersion.setVideoType(videoType);
//          ownerSetter.accept(videoVersion);
//          videoVersions.add(videoVersion);
//      }
//    }

    @Override
    public VideoVersionResponseDTO convertToVideoVersionResponseDTO(VideoVersion videoVersion) {
        VideoVersionResponseDTO videoVersionResponseDTO = VideoVersionResponseDTO.builder()
                .id(videoVersion.getId())
                .movieId(videoVersion.getMovie().getId())
                .videoType(videoVersion.getVideoType())
                .build();

        if(videoVersion.getMovie().getMovieType() == MovieType.STANDALONE) {
            Movie movie = videoVersion.getMovie();
            videoVersionResponseDTO.setMovieTitle(movie.getOriginalTitle());
            videoVersionResponseDTO.setBackdropUrl(movie.getBackdropUrl());
            if(videoVersion.getEpisodes() != null && !videoVersion.getEpisodes().isEmpty()) {
                Episode episodeOfStandaloneMovie = videoVersion.getEpisodes().get(0);
                videoVersionResponseDTO.setEpisodeIdOfStandaloneMovie(episodeOfStandaloneMovie.getId());
            }
        }

        return videoVersionResponseDTO;
    }

    @Override
    public VideoVersionResponseDTO createVideoVersion(long movieId, VideoVersionRequestDTO videoVersionRequestDTO) {
        Movie movie = this.movieRepository.findById(movieId)
                .orElseThrow(() -> new ApplicationException("Không tồn tại phim với id: " + movieId));

        VideoVersion videoVersion = new VideoVersion();
        videoVersion.setVideoType(videoVersionRequestDTO.getVideoType());

        videoVersion.setMovie(movie);

        VideoVersion savedVideoVersion = this.videoVersionRepository.save(videoVersion);

        return this.convertToVideoVersionResponseDTO(savedVideoVersion);
    }

    @Override
    public VideoVersionResponseDTO updateVideoVersion(long videoVersionId, VideoVersionRequestDTO videoVersionRequestDTO) {
        VideoVersion videoVersionDB = this.videoVersionRepository.findById(videoVersionId)
                .orElseThrow(() -> new ApplicationException("Không tồn tại phiên bản video này"));

//        videoVersionDB
        return null;
    }

    @Override
    public void deleteVideoVersion(long videoVersionId) {
        VideoVersion videoVersion = this.videoVersionRepository.findById(videoVersionId)
                .orElseThrow(() -> new ApplicationException("Không tồn tại phiên bản video này"));

        if(videoVersion.getEpisodes() != null && !videoVersion.getEpisodes().isEmpty()) {
            throw new ApplicationException("Bạn phải xóa các tập phim của phiên bản video này trước");
        }
        this.videoVersionRepository.deleteById(videoVersionId);
    }

    @Override
    public List<VideoVersionResponseDTO> getAllVideoVersionsOfMovie(long movieId) {
        Movie movie = this.movieRepository.findById(movieId)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy phim với id: " + movieId));

        List<VideoVersion> videoVersions = movie.getVideoVersions();

        List<VideoVersionResponseDTO> videoVersionResponseDTOS = videoVersions.stream()
                .map(this::convertToVideoVersionResponseDTO)
                .toList();

        return videoVersionResponseDTOS;
    }

    @Override
    public VideoVersionDetailResponseDTO convertToVideoVersionDetailResponseDTO(VideoVersion videoVersion) {
        VideoVersionDetailResponseDTO videoVersionDetailResponseDTO = new VideoVersionDetailResponseDTO();
        videoVersionDetailResponseDTO.setId(videoVersion.getId());
        videoVersionDetailResponseDTO.setVideoType(videoVersion.getVideoType());
        if(videoVersion.getEpisodes() != null && !videoVersion.getEpisodes().isEmpty()) {
            videoVersionDetailResponseDTO.setEpisodeCount(videoVersion.getEpisodes().size());
        }

        return videoVersionDetailResponseDTO;
    }
}
