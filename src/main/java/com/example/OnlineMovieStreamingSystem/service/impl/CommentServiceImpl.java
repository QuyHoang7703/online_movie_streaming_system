package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.Comment;
import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.domain.user.User;
import com.example.OnlineMovieStreamingSystem.domain.user.UserDetail;
import com.example.OnlineMovieStreamingSystem.dto.Meta;
import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.comment.CommentRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.comment.CommentResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.CommentRepository;
import com.example.OnlineMovieStreamingSystem.repository.MovieRepository;
import com.example.OnlineMovieStreamingSystem.repository.UserRepository;
import com.example.OnlineMovieStreamingSystem.service.CommentService;
import com.example.OnlineMovieStreamingSystem.util.SecurityUtil;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final CommentRepository commentRepository;

    @Override
    public CommentResponseDTO createComment(Long movieId, CommentRequestDTO commentRequestDTO) {
        String email = SecurityUtil.getLoggedEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("Không tồn tại tài khoản với email " + email ));


        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ApplicationException("Không tồn tại phim với id " + movieId));



        Comment comment = new Comment();
        comment.setUser(user);
        comment.setMovie(movie);
        comment.setComment(commentRequestDTO.getComment());

        Long parentCommentId = commentRequestDTO.getParentCommentId();
        if(parentCommentId != null) {
            Comment parentComment = this.commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new ApplicationException("Không tồn tại bình luận với id là " + parentCommentId));
            comment.setParentComment(parentComment);
        }


        Comment savedComment = this.commentRepository.save(comment);

        return this.convertToCommentResponseDTO(savedComment);
    }

    @Override
    public ResultPaginationDTO getCommentsForMovie(Long movieId, int page, int size) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ApplicationException("Không tồn tại phim với id " + movieId));

        Pageable pageable = PageRequest.of(page - 1, size, Sort.Direction.DESC, "createAt");
        Page<Comment> commentPage = this.commentRepository.findAllByMovieId(movieId, pageable);

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();

        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(commentPage.getTotalPages());
        meta.setTotalElements(commentPage.getTotalElements());

        List<CommentResponseDTO> commentResponseDTOS = commentPage.getContent().stream()
                .map(this::convertToCommentResponseDTO)
                .toList();

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(commentResponseDTOS);

        return resultPaginationDTO;
    }

    private CommentResponseDTO convertToCommentResponseDTO(Comment comment) {
        UserDetail userDetail = comment.getUser().getUserDetail();
        CommentResponseDTO commentResponseDTO = CommentResponseDTO.builder()
                .id(comment.getId())
                .name(userDetail.getName())
                .avatar(userDetail.getAvatarUrl())
                .comment(comment.getComment())
                .createdAt(comment.getCreateAt())
                .build();

        if(comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            List<CommentResponseDTO> replies = comment.getReplies().stream()
                    .map(this::convertToCommentResponseDTO)
                    .toList();
            commentResponseDTO.setReplies(replies);
            commentResponseDTO.setReplyCount(this.countReplies(comment));
        }
        return commentResponseDTO;
    }

    private int countReplies (Comment comment) {
        if (comment.getReplies() == null || comment.getReplies().isEmpty()) {
            return 0;
        }
        int total = comment.getReplies().stream()
                .map(reply -> 1 + countReplies(reply)) // 1 là bản thân comment đó
                .reduce(0, Integer::sum);

        return total;
    }
}
