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
public class UserInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private double rating;
    private Instant createAt;
    private Instant updateAt;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User users;

    @ManyToOne
    @JoinColumn(name="movie_id")
    private Movie movie;

    @PrePersist
    protected void onCreate() {
        this.createAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateAt = Instant.now();
    }


}
