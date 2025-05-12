package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.request.movie.StandaloneMovieRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.videoVersion.VideoUrlRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.StandaloneMovieResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.StandaloneMovieService;
import com.example.OnlineMovieStreamingSystem.util.annotation.ApiMessage;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/standalone-movies")
public class StandaloneMovieController {
    private final StandaloneMovieService standaloneMovieService;

    @PostMapping
    @ApiMessage("Thêm phim thành công")
    public ResponseEntity<StandaloneMovieResponseDTO> createStandaloneMovie(@RequestPart(name="movieInfo") StandaloneMovieRequestDTO standaloneMovieRequestDTO,
                                                                            @RequestParam("poster")MultipartFile poster,
                                                                            @RequestParam("backdrop") MultipartFile backdrop,
                                                                            @RequestPart(name="voiceOverVideo", required = false) MultipartFile voiceOverVideo,
                                                                            @RequestPart(name="dubbedVideo", required = false) MultipartFile dubbedVideo,
                                                                            @RequestPart(name="vietSubVideo", required = false) MultipartFile vietSubVideo) throws IOException {
//        VideoUrlRequestDTO videoUrlRequestDTO = standaloneMovieRequestDTO.getVideoUrlRequest();
//        String vietSubVideoUrl = videoUrlRequestDTO.getVietSubVideoUrl();
//        String dubbedVideoUrl = videoUrlRequestDTO.getDubbedVideoUrl();
//        String voiceOverVideoUrl = videoUrlRequestDTO.getVoiceOverVideoUrl();
//
//        boolean isAtLeastOneVideoProvided = vietSubVideoUrl != null || dubbedVideoUrl != null || voiceOverVideoUrl != null ||
//                vietSubVideo != null || dubbedVideo != null || voiceOverVideo != null;
//        if (!isAtLeastOneVideoProvided) {
//            throw new ApplicationException("Phải cung cấp ít nhất 1 loại video");
//        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.standaloneMovieService.createStandaloneMovie(standaloneMovieRequestDTO, poster, backdrop));
    }

    @PatchMapping("{movieId}")
    @ApiMessage("Cập nhập phim thành công")
    public ResponseEntity<StandaloneMovieResponseDTO> updateStandaloneMovie(@PathVariable long movieId,
                                                                            @RequestPart(name="movieInfo") StandaloneMovieRequestDTO standaloneMovieRequestDTO,
                                                                            @RequestParam(name="poster", required = false)MultipartFile poster,
                                                                            @RequestParam(name="backdrop", required = false) MultipartFile backdrop,
                                                                            @RequestPart(name="video", required = false) MultipartFile video) throws IOException {

        return ResponseEntity.status(HttpStatus.OK).body(this.standaloneMovieService.updateStandaloneMovie(movieId, standaloneMovieRequestDTO, poster, backdrop, video));
    }

    @GetMapping("{movieId}")
    public ResponseEntity<StandaloneMovieResponseDTO> getStandaloneMovie(@PathVariable long movieId) {

        return ResponseEntity.status(HttpStatus.OK).body(this.standaloneMovieService.getStandaloneMovie(movieId));
    }


}
