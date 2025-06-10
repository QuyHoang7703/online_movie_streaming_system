package com.example.OnlineMovieStreamingSystem.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StandaloneMovie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private double budget;
    private double revenue;


    @OneToOne
    @MapsId
    @JoinColumn(name="id")
    private Movie movie;



}
