package com.example.OnlineMovieStreamingSystem.domain;

import com.example.OnlineMovieStreamingSystem.util.constant.GenderEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String avatarUrl;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String biography;
    private LocalDate birthDate;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String otherName;
    private String placeOfBirth;
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    @OneToMany(mappedBy = "actor")
    private List<MovieActor> movieActors;

}
