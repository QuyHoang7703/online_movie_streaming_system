package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.domain.StandaloneMovie;
import com.example.OnlineMovieStreamingSystem.domain.VideoVersion;
import com.example.OnlineMovieStreamingSystem.dto.request.movie.StandaloneMovieRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.videoVersion.VideoUrlRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.StandaloneMovieResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.videoVersion.VideoVersionResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.MovieRepository;
import com.example.OnlineMovieStreamingSystem.service.ImageStorageService;
import com.example.OnlineMovieStreamingSystem.service.MovieService;
import com.example.OnlineMovieStreamingSystem.service.StandaloneMovieService;
import com.example.OnlineMovieStreamingSystem.service.VideoVersionService;
import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import com.example.OnlineMovieStreamingSystem.util.constant.VideoType;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StandaloneMovieServiceImpl implements StandaloneMovieService {
    private final MovieRepository movieRepository;
    private final MovieService movieService;
    private final ImageStorageService imageStorageService;
    private final VideoVersionService videoVersionService;
    private final String CONTAINER_NAME = "movie-video-container";

    @Transactional
    @Override
    public StandaloneMovieResponseDTO createStandaloneMovie(StandaloneMovieRequestDTO standaloneMovieRequestDTO,
                                                            MultipartFile poster,
                                                            MultipartFile backdrop) throws IOException {

        Movie movie = this.movieService.createMovieFromDTO(standaloneMovieRequestDTO, poster, backdrop);

        //  Set attributes for standalone movie
        StandaloneMovie standaloneMovie = new StandaloneMovie();
        standaloneMovie.setRevenue(standaloneMovieRequestDTO.getRevenue());
        standaloneMovie.setBudget(standaloneMovieRequestDTO.getBudget());
//        standaloneMovie.setDuration(standaloneMovieRequestDTO.getDuration());

        standaloneMovie.setMovie(movie);
        movie.setStandaloneMovie(standaloneMovie);

        // Set video version for movie
//        VideoUrlRequestDTO videoUrlRequestDTO = standaloneMovieRequestDTO.getVideoUrlRequest();
//        String voiceOverVideoUrl = videoUrlRequestDTO.getVoiceOverVideoUrl();
//        String dubbedVideoUrl = videoUrlRequestDTO.getDubbedVideoUrl();
//        String vietSubVideoUrl = videoUrlRequestDTO.getVietSubVideoUrl();

//        List<VideoVersion> videoVersions = new ArrayList<>();
//
//        voiceOverVideoUrl = this.videoVersionService.resolveVideoUrl(voiceOverVideoUrl, voiceOverVideo);
//        dubbedVideoUrl = this.videoVersionService.resolveVideoUrl(dubbedVideoUrl, dubbedVideo);
//        vietSubVideoUrl = this.videoVersionService.resolveVideoUrl(vietSubVideoUrl, vietsubVideo);
//
//        this.videoVersionService.addVideoVersionIfAvailable(videoVersions, voiceOverVideoUrl, VideoType.VOICEOVER, v -> v.setStandaloneMovie(standaloneMovie));
//        this.videoVersionService.addVideoVersionIfAvailable(videoVersions, dubbedVideoUrl, VideoType.DUBBED, v -> v.setStandaloneMovie(standaloneMovie));
//        this.videoVersionService.addVideoVersionIfAvailable(videoVersions, vietSubVideoUrl, VideoType.VIETSUB, v -> v.setStandaloneMovie(standaloneMovie));
//
//        if(!videoVersions.isEmpty()) {
//            standaloneMovie.setVideoVersions(videoVersions);
//        }

        Movie savedMovie = this.movieRepository.save(movie);

        return this.convertToStandaloneMovieResponseDTO(savedMovie);
    }

    @Transactional
    @Override
    public StandaloneMovieResponseDTO updateStandaloneMovie(long movieId,
                                                            StandaloneMovieRequestDTO standaloneMovieRequestDTO,
                                                            MultipartFile poster,
                                                            MultipartFile backdrop,
                                                            MultipartFile video) throws IOException {
        Movie updateMovie = this.movieService.updateMovieFromDTO(movieId, standaloneMovieRequestDTO, poster, backdrop);
        StandaloneMovie standaloneMovie = updateMovie.getStandaloneMovie();

        if(!Objects.equals(standaloneMovie.getBudget(), standaloneMovieRequestDTO.getBudget())) {
           standaloneMovie.setBudget(standaloneMovieRequestDTO.getBudget());
        }

        if(!Objects.equals(standaloneMovie.getRevenue(), standaloneMovieRequestDTO.getRevenue())) {
           standaloneMovie.setRevenue(standaloneMovieRequestDTO.getRevenue());
        }

//        if(!Objects.equals(standaloneMovie.getDuration(), standaloneMovieRequestDTO.getDuration())) {
//           standaloneMovie.setDuration(standaloneMovieRequestDTO.getDuration());
//        }


//        String currentVideoUrl = standaloneMovie.getVideoUrl();
//        if(video != null && !video.isEmpty()) {
//            String newVideoUrl = this.imageStorageService.uploadFile(CONTAINER_NAME, video.getOriginalFilename(), video.getInputStream());
//            if(currentVideoUrl != null && !currentVideoUrl.trim().isEmpty()) {
//                String originVideoName = standaloneMovie.getVideoUrl().substring(currentVideoUrl.lastIndexOf("/" ) + 1);
//                this.imageStorageService.deleteFile(CONTAINER_NAME, originVideoName);
//            }
//            standaloneMovie.setVideoUrl(newVideoUrl);
//
//        } else if (standaloneMovie.getVideoUrl() != null && !standaloneMovieRequestDTO.getVideoUrl().trim().isEmpty()) {
//            if(!Objects.equals(currentVideoUrl, standaloneMovieRequestDTO.getVideoUrl())) {
//                if(currentVideoUrl != null && currentVideoUrl.contains("blob.core.windows.net")) {
//                    String originVideoName = standaloneMovie.getVideoUrl().substring(currentVideoUrl.lastIndexOf("/" ) + 1);
//                    this.imageStorageService.deleteFile(CONTAINER_NAME, originVideoName);
//                }
//                standaloneMovie.setVideoUrl(standaloneMovieRequestDTO.getVideoUrl());
//            }
//        }

        updateMovie.setStandaloneMovie(standaloneMovie);
        Movie updatedMovie = this.movieRepository.save(updateMovie);

        return this.convertToStandaloneMovieResponseDTO(updatedMovie);
    }

    @Override
    public StandaloneMovieResponseDTO getStandaloneMovie(long movieId) {
        Movie movie = this.movieRepository.findById(movieId)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy phim"));
        if(movie.getMovieType() != MovieType.STANDALONE){
            throw new ApplicationException("Phim này không phải là phim lẻ");
        }
        StandaloneMovieResponseDTO standaloneMovieResponseDTO = this.convertToStandaloneMovieResponseDTO(movie);

        return standaloneMovieResponseDTO;
    }

    @Override
    public StandaloneMovieResponseDTO convertToStandaloneMovieResponseDTO(Movie movie){
        StandaloneMovieResponseDTO standaloneMovieResponseDTO = this.movieService.convertToMovieInfoDTO(movie, StandaloneMovieResponseDTO.class);
        standaloneMovieResponseDTO.setBudget(movie.getStandaloneMovie().getBudget());
        standaloneMovieResponseDTO.setRevenue(movie.getStandaloneMovie().getRevenue());
//        standaloneMovieResponseDTO.setDuration(movie.getStandaloneMovie().getDuration());

//        List<VideoVersion> videoVersions = movie.getStandaloneMovie().getVideoVersions();
//        List<VideoVersionResponseDTO> videoVersionResponseDTOs = videoVersions.stream()
//                .map(this.videoVersionService::convertToVideoVersionResponseDTO)
//                .toList();
//
//        standaloneMovieResponseDTO.setVideoVersions(videoVersionResponseDTOs);

        return standaloneMovieResponseDTO;
    }


}
