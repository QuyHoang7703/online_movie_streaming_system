package com.example.OnlineMovieStreamingSystem.repository;

import com.example.OnlineMovieStreamingSystem.domain.Actor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {
    @Query("SELECT a FROM Actor a " +
            "WHERE (:actorName  IS NULL OR :actorName = '') " +
            "OR LOWER(a.name) LIKE LOWER(CONCAT('%', :actorName, '%'))")
    Page<Actor> findAll (@Param("actorName") String actorName, Pageable pageable);

    List<Actor> findByIdIn(List<Long> actorIds);
}
