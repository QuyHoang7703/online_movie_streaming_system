package com.example.OnlineMovieStreamingSystem.repository;

import com.example.OnlineMovieStreamingSystem.domain.FavoriteMovie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteMovieRepository extends JpaRepository<FavoriteMovie, Long> {
    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    void deleteByUser_IdAndMovie_Id(Long userId, Long movieId);

    @Query("SELECT fm FROM FavoriteMovie fm " +
            "JOIN fm.user u " +
            "WHERE u.email = :email " +
            "ORDER BY fm.createdAt DESC ")
    Page<FavoriteMovie> getFavoriteMovieByEmail(@Param("email") String email, Pageable pageable);
}
