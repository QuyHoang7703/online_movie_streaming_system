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
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT DISTINCT m FROM Movie m " +
            "LEFT JOIN m.genres g " +
            "WHERE (:title IS NULL OR :title = '' OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:genreNames IS NULL OR LOWER(g.name) IN :genreNames) " +
            "AND (:movieType IS NULL OR m.movieType = :movieType) " +
            "AND (:countries IS NULL OR m.country IN :countries)")
    Page<Movie> findMoviesByFilter(
            @Param("title") String title,
            @Param("genreNames") List<String> genreNames,
            @Param("movieType") MovieType movieType,
            @Param("countries") List<String> countries,
            Pageable pageable
    );


    @Query("SELECT DISTINCT m.country FROM Movie m " +
            "WHERE m.country IS NOT NULL")
    List<String> getAllCountriesOfMovies();

    Optional<Movie> findByIdAndMovieType(long id, MovieType movieType);
}
