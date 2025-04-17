package com.example.OnlineMovieStreamingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Meta {
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalElements;
}
