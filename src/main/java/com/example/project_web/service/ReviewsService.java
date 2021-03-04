package com.example.project_web.service;

import com.example.project_web.model.Review;
import com.example.project_web.model.User;
import com.example.project_web.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewsService implements ReviewsServiceInterface{
    private final ReviewRepository reviewRepository;

    public ReviewsService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<Review> findAllByUserReviewed(User user) {
        return this.reviewRepository.findAllByReviewed(user);
    }
}
