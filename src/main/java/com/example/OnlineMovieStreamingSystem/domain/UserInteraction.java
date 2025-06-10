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
    private Instant updatedAt;  // lưu timestamp dạng số nguyên (epoch seconds)

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="movie_id")
    private Movie movie;

    private long userTemporaryId;
    private long movieTemporaryId;

    @PrePersist
    @PreUpdate
    protected void onCreate() {
        updatedAt = Instant.now();
    }



}
