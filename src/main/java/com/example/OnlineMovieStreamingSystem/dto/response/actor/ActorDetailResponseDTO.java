package com.example.OnlineMovieStreamingSystem.dto.response.actor;

import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieUserResponseDTO;
import com.example.OnlineMovieStreamingSystem.util.constant.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActorDetailResponseDTO {
    private long id;
    private String name;
    private LocalDate birthDate;
    private String avatarUrl;
    private String biography;
    private String otherName;
    private String placeOfBirth;
    private GenderEnum gender;
    private List<MovieUserResponseDTO> movies;

    // Có thêm movie dto ở đây

}
