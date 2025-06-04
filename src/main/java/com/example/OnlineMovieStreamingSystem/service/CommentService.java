package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.comment.CommentRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.comment.CommentResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    CommentResponseDTO createComment(Long movieId, CommentRequestDTO commentRequestDTO);
    ResultPaginationDTO getCommentsForMovie(Long movieId, int page, int size);
}
