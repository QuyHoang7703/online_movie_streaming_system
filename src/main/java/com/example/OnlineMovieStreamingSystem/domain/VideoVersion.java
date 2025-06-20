package com.example.OnlineMovieStreamingSystem.domain;

import com.example.OnlineMovieStreamingSystem.util.constant.VideoType;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VideoVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    private VideoType videoType;

    @ManyToOne
    @JoinColumn(name="movie_id")
    private Movie movie;

    @OneToMany(mappedBy = "videoVersion",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Episode> episodes;


}
