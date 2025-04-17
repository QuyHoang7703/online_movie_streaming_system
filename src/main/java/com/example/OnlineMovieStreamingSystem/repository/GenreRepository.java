package com.example.OnlineMovieStreamingSystem.repository;

import com.example.OnlineMovieStreamingSystem.domain.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    @Query("SELECT g FROM Genre g " +
            "WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :genreName, '%'))")
    Page<Genre> findAll(@Param("genreName") String genreName, Pageable pageable);
}
