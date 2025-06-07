package com.example.OnlineMovieStreamingSystem.repository;

import com.example.OnlineMovieStreamingSystem.domain.user.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailRepository extends JpaRepository<UserDetail, Long> {
}
