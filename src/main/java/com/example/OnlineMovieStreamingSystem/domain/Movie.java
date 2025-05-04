package com.example.OnlineMovieStreamingSystem.domain;

import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private String director;
    private String posterUrl;
    private String backdropUrl;
    private String country;
    private LocalDate releaseDate;
    private boolean free;
    private String trailerUrl;
    @Enumerated(EnumType.STRING)
    private MovieType movieType;
    private Instant createAt;
    private Instant updateAt;


    @OneToOne(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private StandaloneMovie standaloneMovie;

    @OneToOne(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private SeriesMovie seriesMovie;

    @ManyToMany
    @JoinTable(name="movie_genre", joinColumns = @JoinColumn(name="movie_id"), inverseJoinColumns = @JoinColumn(name="genre_id"))
    private List<Genre> genres;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieActor> movieActors;

    @ManyToMany
    @JoinTable(name="movie_subscription_plan",
            joinColumns = @JoinColumn(name="movie_id"),
            inverseJoinColumns = @JoinColumn(name="subscription_plan_id"))
    private List<SubscriptionPlan> subscriptionPlans;

    @PrePersist
    protected void prePersist() {
        this.createAt = Instant.now();
    }

    @PreUpdate
    protected void preUpdate() {
        this.updateAt = Instant.now();
    }
}
