package com.example.OnlineMovieStreamingSystem.controller.user;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.userInteraction.UserInteractionRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.userInteraction.UserInteractionResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.UserInteractionService;
import com.example.OnlineMovieStreamingSystem.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user-interactions")
@RequiredArgsConstructor
public class UserInteractionController {
    private final UserInteractionService userInteractionService;

    @PostMapping
    @ApiMessage("Thêm đánh giá thành công")
    public ResponseEntity<UserInteractionResponseDTO> ratingForMovie(@RequestBody UserInteractionRequestDTO userInteractionRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userInteractionService.addUserInteraction(userInteractionRequestDTO));
    }

    @PatchMapping
    @ApiMessage("Cập nhập đánh giá thành công")
    public ResponseEntity<UserInteractionResponseDTO> updateRatingForMovie(@RequestBody UserInteractionRequestDTO userInteractionRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userInteractionService.updateUserInteraction(userInteractionRequestDTO));
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<UserInteractionResponseDTO> getRatingOfUserForMovie(@PathVariable("movieId") long movieId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userInteractionService.getUserInteraction(movieId));
    }

    @GetMapping("/history-view")
    public ResponseEntity<ResultPaginationDTO> getHistoryViewOfUser(@RequestParam(name = "page", defaultValue = "1") int page,
                                                                    @RequestParam(name = "size", defaultValue = "15") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userInteractionService.getHistoryViewForUser(page, size));
    }


}
