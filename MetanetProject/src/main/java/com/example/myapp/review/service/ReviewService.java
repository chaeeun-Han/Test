package com.example.myapp.review.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.myapp.review.dao.IReviewRepository;
import com.example.myapp.review.model.Review;

@Service
public class ReviewService implements IReviewService {

    @Autowired
    IReviewRepository iReviewDao;

    @Override
    public void registerReview(Review review) {
        iReviewDao.registerReview(review);
    }

    @Override
    public List<Review> getReviews(Long lectureId) {
        return iReviewDao.getReviews(lectureId);
    }

    @Override
    public void updateReview(Review review) {
        iReviewDao.updateReview(review);
    }

    @Override
    public void deleteReview(Review review) {
        iReviewDao.deleteReview(review);
    }

}
