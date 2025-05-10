package com.example.OnlineMovieStreamingSystem.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StandaloneMovie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Integer duration;
    private String videoUrl;

    @OneToOne
    @MapsId
    @JoinColumn(name="id")
    private Movie movie;

}
