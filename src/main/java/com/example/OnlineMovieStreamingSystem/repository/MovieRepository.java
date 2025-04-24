package com.example.OnlineMovieStreamingSystem.repository;

import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    @Query("SELECT DISTINCT m FROM Movie m " +
            "JOIN m.genres g " +
            "WHERE (:title IS NULL OR :title = '' OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:genreNames IS NULL OR g.name IN :genreNames) " +
            "AND (:movieType IS NULL OR m.movieType = :movieType)")
    Page<Movie> findMoviesByFilter(@Param("title") String title,
                                   @Param("genreNames") List<String> genreNames,
                                   @Param("movieType") MovieType movieType,
                                   Pageable pageable);

}
