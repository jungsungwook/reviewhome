package com.memeki.reviewhome.review.service;

import org.springframework.stereotype.Service;

import com.memeki.reviewhome.global.exception.DefaultException;
import com.memeki.reviewhome.global.exceptionHandler.ErrorCode;
import com.memeki.reviewhome.review.entity.Review;
import com.memeki.reviewhome.review.repository.ReviewRepository;

@Service
public class ReviewService {

    private ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review getReviewByReviewId(int reviewId) throws Exception {
        try {
            return reviewRepository.findReviewById(reviewId);
        } catch (Exception e) {
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }
    }

    public String createReview(String targetId) throws Exception {
        try {
            Review review = new Review();
            review.setTargetId(targetId);
            reviewRepository.save(review);
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Review not created");
        }
    }
}