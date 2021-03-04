package com.example.project_web.service;

import com.example.project_web.model.Review;
import com.example.project_web.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReviewsServiceInterface {
    List<Review> findAllByUserReviewed(User user);
}
