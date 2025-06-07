package com.example.OnlineMovieStreamingSystem.domain.user;

import com.example.OnlineMovieStreamingSystem.domain.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String email;
    private String password;
    private boolean active;

    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserDetail userDetail;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteMovie> favoriteMovies;

    @OneToMany(mappedBy = "user")
    private List<SubscriptionOrder> subscriptionOrders;

    @OneToMany(mappedBy = "user")
    private List<UserNotification> userNotifications;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserInteraction> userInteractions;
}
