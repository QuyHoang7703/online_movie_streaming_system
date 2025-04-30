package com.example.OnlineMovieStreamingSystem.repository;

import com.example.OnlineMovieStreamingSystem.domain.SeriesMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeriesMovieRepository extends JpaRepository<SeriesMovie, Long> {
    Optional<SeriesMovie> findById(Long id);
}
