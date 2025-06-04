package com.example.OnlineMovieStreamingSystem.dto.request.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDTO {
    @NotBlank(message = "Bình luận không thể đế trống")
    private String comment;
    private Long parentCommentId;
}
