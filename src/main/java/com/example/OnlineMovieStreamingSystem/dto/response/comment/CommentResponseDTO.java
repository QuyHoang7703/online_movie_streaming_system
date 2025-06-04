package com.example.OnlineMovieStreamingSystem.dto.response.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {
    private long id;
    private String name;
    private String avatar;
    private String comment;
    private Instant createdAt;
    private int replyCount;
    private List<CommentResponseDTO> replies;
}
