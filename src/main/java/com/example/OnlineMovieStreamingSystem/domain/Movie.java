package com.example.OnlineMovieStreamingSystem.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    @Id
    private int id;
    private String name;
    private String title;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private String director;
    private String posterPath;
    private String backdropPath;
    private String country;
    private Instant releaseDate;
    private boolean isPaid;
    private double price;
    private Instant createAt;
    private Instant updateAt;

    @OneToOne(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private StandaloneMovie standaloneMovie;

    @OneToOne(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private SeriesMovie seriesMovie;

    @ManyToMany
    @JoinTable(name="movie_genre", joinColumns = @JoinColumn(name="movie_id"), inverseJoinColumns = @JoinColumn(name="genre_id"))
    private List<Genre> genres;

    @OneToMany(mappedBy = "movie")
    private List<MovieActor> movieActors;


    @PrePersist
    protected void prePersist() {
        this.createAt = Instant.now();
    }

    @PreUpdate
    protected void preUpdate() {
        this.updateAt = Instant.now();
    }
}
