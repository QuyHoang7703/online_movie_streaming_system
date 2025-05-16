package com.example.OnlineMovieStreamingSystem.domain;

import com.example.OnlineMovieStreamingSystem.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name="favorite_movie",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id"})
)
public class FavoriteMovie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name="movie_id", nullable=false)
    private Movie movie;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

}
