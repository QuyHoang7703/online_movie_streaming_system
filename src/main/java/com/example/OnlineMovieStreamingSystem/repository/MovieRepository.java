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
            "LEFT JOIN m.countries c " +
            "WHERE (:title IS NULL OR :title = '' OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
            "OR LOWER(m.originalTitle) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:genreNames IS NULL OR LOWER(g.name) IN :genreNames) " +
            "AND (:movieType IS NULL OR m.movieType = :movieType) " +
            "AND (:countries IS NULL OR c.name IN :countries)")
    Page<Movie> findMoviesByFilter(
            @Param("title") String title,
            @Param("genreNames") List<String> genreNames,
            @Param("movieType") MovieType movieType,
            @Param("countries") List<String> countries,
            Pageable pageable
    );


    @Query("SELECT m FROM Movie m " +
            "LEFT JOIN m.genres g " +
            "LEFT JOIN m.subscriptionPlans sp " +
            "LEFT join m.countries c " + // Join với subscriptionPlans
            "WHERE (:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:genreNames IS NULL OR g.name IN :genreNames) " +
            "AND (:movieType IS NULL OR m.movieType = :movieType) " +
            "AND (:countries IS NULL OR c.name IN :countries) " +
            "AND (:planIds IS NULL OR sp.id IN :planIds) " + // Thêm điều kiện lọc theo planIds
            "GROUP BY m.id") // Đảm bảo không trùng lặp phim nếu có nhiều genre hoặc plan
    Page<Movie> findMoviesByFilterAndSubscriptionPlans(@Param("title") String title,
                                                       @Param("genreNames") List<String> genreNames,
                                                       @Param("movieType") MovieType movieType,
                                                       @Param("countries") List<String> countries,
                                                       @Param("planIds") List<Long> planIds, // Tham số mới
                                                       Pageable pageable);

    List<Movie> findByTmdbIdInAndMovieType(List<Long> tmdbIds, MovieType movieType);

    @Query("SELECT DISTINCT m FROM Movie m " +
            "LEFT JOIN m.countries c " +
            "WHERE (:movieType IS NULL OR m.movieType = :movieType) " +
            "AND (:country IS NULL OR c.name = :country)")
    Page<Movie> getHotMoviesByFilter(@Param("movieType") MovieType movieType,
                                     @Param("country") String country,
                                     Pageable pageable);

    long countByMovieType(MovieType movieType);


    List<Movie> findByTmdbIdIn(List<Long> tmdbIds);



}
