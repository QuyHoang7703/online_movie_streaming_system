package com.example.OnlineMovieStreamingSystem.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "genres")
    private List<Movie> movies;
}
