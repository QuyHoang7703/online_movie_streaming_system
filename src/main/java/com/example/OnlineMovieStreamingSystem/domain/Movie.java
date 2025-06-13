package com.example.OnlineMovieStreamingSystem.domain;

import com.example.OnlineMovieStreamingSystem.domain.user.User;
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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"id", "movieType"}))
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private String originalTitle;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private String director;
    private String posterUrl;
    private String backdropUrl;
    private LocalDate releaseDate;
    private boolean free;
    private String trailerUrl;
    @Enumerated(EnumType.STRING)
    private MovieType movieType;
    private String status;
    private double voteAverage;
    private double voteCount;
    private String quality;
    private Instant createAt;
    private Instant updateAt;
    private long tmdbId;


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

    @ManyToMany
    @JoinTable(name="movie_country", joinColumns = @JoinColumn(name="movie_id"), inverseJoinColumns = @JoinColumn(name="country_id"))
    private List<Country> countries;

    @OneToMany(mappedBy = "movie")
    private List<VideoVersion> videoVersions;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteMovie> favoriteMovies;

    @OneToMany(mappedBy = "movie")
    private List<Comment> comments;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<UserInteraction> userInteractions;

    @PrePersist
    protected void prePersist() {
        this.createAt = Instant.now();
    }

    @PreUpdate
    protected void preUpdate() {
        this.updateAt = Instant.now();
    }
}
