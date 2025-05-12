package com.example.OnlineMovieStreamingSystem.domain;

import jakarta.persistence.*;
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
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private int episodeNumber;
    private int duration;
    private String videoUrl;

    @ManyToOne
    @JoinColumn(name="video_version_id")
    private VideoVersion videoVersion;



}
