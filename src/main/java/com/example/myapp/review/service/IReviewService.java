package com.example.myapp.review.service;

import java.util.List;

import com.example.myapp.review.model.Review;

public interface IReviewService {
    void registerReview(Review review);

    List<Review> getReviews(Long lectureId);

    void updateReview(Review review);

    void deleteReview(Review review);
}
