package com.example.OnlineMovieStreamingSystem.domain.user;

import com.example.OnlineMovieStreamingSystem.util.constant.GenderEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetail {
    @Id
    private String id;
    private String name;
    private String phoneNumber;
    private String address;
    private String avatarUrl;
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    @OneToOne
    @MapsId
    @JoinColumn(name="id")
    private User user;

}
