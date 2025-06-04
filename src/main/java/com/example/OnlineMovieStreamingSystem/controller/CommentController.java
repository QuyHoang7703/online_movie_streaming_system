package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.comment.CommentRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.comment.CommentResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("{movieId}")
    public ResponseEntity<CommentResponseDTO> createComment(@PathVariable("movieId") Long movieId,
                                                            @RequestBody CommentRequestDTO commentRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.commentService.createComment(movieId, commentRequestDTO));
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<ResultPaginationDTO> getCommentsForMovie(@PathVariable("movieId") Long movieId,
                                                                   @RequestParam(name= "page", defaultValue = "1") int page,
                                                                   @RequestParam(name= "size" , defaultValue = "10") int size) {

        return ResponseEntity.status(HttpStatus.OK).body(this.commentService.getCommentsForMovie(movieId, page, size));
    }


}
