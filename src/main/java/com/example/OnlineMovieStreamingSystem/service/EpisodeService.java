package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.episode.EpisodeRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.episode.EpisodeDetailResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface EpisodeService {
    EpisodeDetailResponseDTO createEpisode(long videoVersionId, EpisodeRequestDTO episodeRequestDTO,
                                           MultipartFile video) throws IOException;

    ResultPaginationDTO getEpisodeList(long seriesMovieId, int page, int size);

    EpisodeDetailResponseDTO getEpisodeDetail(long episodeId);

    EpisodeDetailResponseDTO updateEpisode(long episodeId, EpisodeRequestDTO episodeRequestDTO, MultipartFile video) throws IOException;

    void deleteEpisode(long episodeId);
}
