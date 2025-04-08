package com.example.OnlineMovieStreamingSystem.domain.user;

import com.example.OnlineMovieStreamingSystem.util.GenderEnum;
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
public class UserDetail {
    @Id
    private String id;

    @OneToOne
    @MapsId
    @JoinColumn(name="id")
    private User user;

    private String name;
    private String phoneNumber;
    private String address;
    private String avatarUrl;
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

}
