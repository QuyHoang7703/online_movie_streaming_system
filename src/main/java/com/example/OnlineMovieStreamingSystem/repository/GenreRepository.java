package com.example.OnlineMovieStreamingSystem.repository;

import com.example.OnlineMovieStreamingSystem.domain.Genre;
import com.example.OnlineMovieStreamingSystem.dto.response.statistic.GenreStatisticDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    boolean existsByName(String name);

    @Query("SELECT g FROM Genre g " +
            "WHERE (:genreName IS NULL OR :genreName = '') " +
            "OR LOWER(g.name) LIKE LOWER(CONCAT('%', :genreName, '%'))")
    Page<Genre> findAll(@Param("genreName") String genreName, Pageable pageable);

    List<Genre> findByIdIn (List<Long> genreIds);

    @Query("SELECT new com.example.OnlineMovieStreamingSystem.dto.response.statistic.GenreStatisticDTO(" +
            "g.name, COUNT(g.name))  FROM Genre g " +
            "JOIN g.movies m " +
            "GROUP BY g.name")
    List<GenreStatisticDTO> getGenreStatistics();
}
