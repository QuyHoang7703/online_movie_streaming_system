package com.example.OnlineMovieStreamingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultPaginationDTO{
    private Meta meta;
    private Object result;
}
