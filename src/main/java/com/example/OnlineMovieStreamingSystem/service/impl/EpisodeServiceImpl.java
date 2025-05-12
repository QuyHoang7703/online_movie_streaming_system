package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.Episode;
import com.example.OnlineMovieStreamingSystem.domain.SeriesMovie;
import com.example.OnlineMovieStreamingSystem.domain.VideoVersion;
import com.example.OnlineMovieStreamingSystem.dto.Meta;
import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.episode.EpisodeRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.episode.EpisodeDetailResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.episode.EpisodeSummaryResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.EpisodeRepository;
import com.example.OnlineMovieStreamingSystem.repository.SeriesMovieRepository;
import com.example.OnlineMovieStreamingSystem.repository.VideoVersionRepository;
import com.example.OnlineMovieStreamingSystem.service.EpisodeService;
import com.example.OnlineMovieStreamingSystem.service.ImageStorageService;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EpisodeServiceImpl implements EpisodeService {
    private final EpisodeRepository episodeRepository;
    private final ImageStorageService imageStorageService;
    private final VideoVersionRepository videoVersionRepository;
    private final String CONTAINER_NAME = "movie-video-container";

    @Override
    public EpisodeDetailResponseDTO createEpisode(long videoVersionId, EpisodeRequestDTO episodeRequestDTO, MultipartFile video) throws IOException {
        VideoVersion videoVersion = this.videoVersionRepository.findById(videoVersionId)
                .orElseThrow(() -> new ApplicationException("Không có phiên bản video với id: " + videoVersionId));
        Episode episode = new Episode();
        episode.setTitle(episodeRequestDTO.getTitle());
        episode.setEpisodeNumber(episodeRequestDTO.getEpisodeNumber());
        episode.setDuration(episodeRequestDTO.getDuration());


        if(video != null && !video.isEmpty())  {
            String videoUrl = this.imageStorageService.uploadFile(CONTAINER_NAME, video.getOriginalFilename(), video.getInputStream());
            if(videoUrl != null) {
                episode.setVideoUrl(videoUrl);
            }
        }else if(episodeRequestDTO.getVideoUrl() != null) {
            episode.setVideoUrl(episodeRequestDTO.getVideoUrl());
        }

        episode.setVideoVersion(videoVersion);

        Episode savedEpisode = this.episodeRepository.save(episode);

        return this.convertToEpisodeDetailResponseDTO(savedEpisode);
    }

    @Override
    public ResultPaginationDTO getEpisodeList(long videoVersionId, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Episode> episodePage = this.episodeRepository.findByVideoVersionId(videoVersionId, pageable);

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();

        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(episodePage.getTotalPages());
        meta.setTotalElements(episodePage.getTotalElements());

        resultPaginationDTO.setMeta(meta);

        List<EpisodeSummaryResponseDTO> episodeSummaryResponseDTOS = episodePage.getContent().stream()
                .map(this::convertToEpisodeSummaryResponseDTO)
                .toList();

        resultPaginationDTO.setResult(episodeSummaryResponseDTOS);

        return resultPaginationDTO;
    }

    @Override
    public EpisodeDetailResponseDTO getEpisodeDetail(long episodeId) {
        Episode episode = this.episodeRepository.findById(episodeId)
                .orElseThrow(() -> new ApplicationException("Không tồn tại tập phim với id là " + episodeId));

        return this.convertToEpisodeDetailResponseDTO(episode);
    }

    @Override
    public EpisodeDetailResponseDTO updateEpisode(long episodeId,
                                                  EpisodeRequestDTO episodeRequestDTO,
                                                  MultipartFile video) throws IOException {
        Episode episodeDB = this.episodeRepository.findById(episodeId)
                .orElseThrow(() -> new ApplicationException("Không tồn tại tập phim với id là " + episodeId));

        if(!Objects.equals(episodeDB.getTitle(), episodeRequestDTO.getTitle())) {
            episodeDB.setTitle(episodeRequestDTO.getTitle());
        }
        if(!Objects.equals(episodeDB.getEpisodeNumber(), episodeRequestDTO.getEpisodeNumber())) {
            episodeDB.setEpisodeNumber(episodeRequestDTO.getEpisodeNumber());
        }
        if(!Objects.equals(episodeDB.getDuration(), episodeRequestDTO.getDuration())) {
            episodeDB.setDuration(episodeRequestDTO.getDuration());
        }

        String currentVideoUrl = episodeDB.getVideoUrl();
        if(video != null && !video.isEmpty()) {
            String newVideoUrl = this.imageStorageService.uploadFile(CONTAINER_NAME, video.getOriginalFilename(), video.getInputStream());
           // Delete old video url for episode
            if(currentVideoUrl != null && !currentVideoUrl.trim().isEmpty()) {
                String originVideoName = episodeDB.getVideoUrl().substring(currentVideoUrl.lastIndexOf("/" ) + 1);
                this.imageStorageService.deleteFile(CONTAINER_NAME, originVideoName);
            }

            episodeDB.setVideoUrl(newVideoUrl);

        } else if (episodeRequestDTO.getVideoUrl() != null && !episodeRequestDTO.getVideoUrl().trim().isEmpty()) {
            if(!Objects.equals(currentVideoUrl, episodeRequestDTO.getVideoUrl())) {
                if(currentVideoUrl != null && currentVideoUrl.contains("blob.core.windows.net")) {
                    String originVideoName = episodeDB.getVideoUrl().substring(currentVideoUrl.lastIndexOf("/" ) + 1);
                    this.imageStorageService.deleteFile(CONTAINER_NAME, originVideoName);
                }
                episodeDB.setVideoUrl(episodeRequestDTO.getVideoUrl());
            }
        }

        Episode updatedEpisode = this.episodeRepository.save(episodeDB);

        return this.convertToEpisodeDetailResponseDTO(updatedEpisode);
    }

    @Override
    public void deleteEpisode(long episodeId) {
        Episode episode = this.episodeRepository.findById(episodeId)
                .orElseThrow(() -> new ApplicationException("Không tồn tại tập phim với id " + episodeId));

        this.episodeRepository.delete(episode);
    }

    private EpisodeDetailResponseDTO convertToEpisodeDetailResponseDTO(Episode episode) {
        EpisodeDetailResponseDTO episodeDetailResponseDTO = EpisodeDetailResponseDTO.builder()
                .id(episode.getId())
                .title(episode.getTitle())
                .episodeNumber(episode.getEpisodeNumber())
                .duration(episode.getDuration())
                .videoUrl(episode.getVideoUrl())
                .videoVersionId(episode.getVideoVersion().getId())
                .build();
        return episodeDetailResponseDTO;
    }

    private EpisodeSummaryResponseDTO convertToEpisodeSummaryResponseDTO(Episode episode) {
        EpisodeSummaryResponseDTO episodeSummaryResponseDTO = EpisodeSummaryResponseDTO.builder()
                .id(episode.getId())
                .title(episode.getTitle())
                .episodeNumber(episode.getEpisodeNumber())
                .build();

        return episodeSummaryResponseDTO;
    }




}
