package com.example.project_web.repository;

import com.example.project_web.model.Review;
import com.example.project_web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByReviewed(User user);
}
