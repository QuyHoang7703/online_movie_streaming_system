package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.episode.EpisodeRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.episode.EpisodeDetailResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.EpisodeService;
import com.example.OnlineMovieStreamingSystem.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class EpisodeController {
    private final EpisodeService episodeService;

    @PostMapping("/video-versions/{videoVersionId}/episodes")
    @ApiMessage("Thêm tập phim thành công")
    public ResponseEntity<EpisodeDetailResponseDTO> createEpisode(@PathVariable("videoVersionId") long videoVersionId,
                                                                  @RequestPart("episodeInfo") EpisodeRequestDTO episodeRequestDTO,
                                                                  @RequestPart(name="video", required = false) MultipartFile video) throws IOException {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.episodeService.createEpisode(videoVersionId, episodeRequestDTO, video));
    }

    @GetMapping("/video-versions/{videoVersionId}/episodes")
    public ResponseEntity<ResultPaginationDTO> getEpisodeOfSeriesMovie(@PathVariable("videoVersionId") long videoVersionId,
                                                                       @RequestParam(name = "page", defaultValue = "1") int page,
                                                                       @RequestParam(name = "size", defaultValue = "50") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(this.episodeService.getEpisodeList(videoVersionId, page,size));
    }

    @GetMapping("/episodes/{episodeId}")
    public ResponseEntity<EpisodeDetailResponseDTO> getEpisodeDetail(@PathVariable("episodeId") long episodeId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.episodeService.getEpisodeDetail(episodeId));
    }

    @PatchMapping("/episodes/{episodeId}")
    @ApiMessage("Cập nhập tập phim thành công")
    public ResponseEntity<EpisodeDetailResponseDTO> updateEpisode(@PathVariable("episodeId") long episodeId,
                                                                  @RequestPart("episodeInfo") EpisodeRequestDTO episodeRequestDTO,
                                                                  @RequestPart(name="video", required = false) MultipartFile video) throws IOException {

        return ResponseEntity.status(HttpStatus.OK).body(this.episodeService.updateEpisode(episodeId, episodeRequestDTO, video));
    }

    @DeleteMapping("/episodes/{episodeId}")
    @ApiMessage("Xóa tập phim thành công")
    public ResponseEntity<Void> deleteEpisode(@PathVariable("episodeId") long episodeId) {
        this.episodeService.deleteEpisode(episodeId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
