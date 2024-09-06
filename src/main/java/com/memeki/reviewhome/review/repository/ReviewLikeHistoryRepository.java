package com.memeki.reviewhome.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.review.entity.ReviewLikeHistory;

public interface ReviewLikeHistoryRepository extends JpaRepository<ReviewLikeHistory, Integer> {
    ReviewLikeHistory findReviewLikeHistoryByUserIdAndReviewId(Long userId, int reviewId);
    ReviewLikeHistory findReviewLikeHistoryByReviewId(int reviewId);
    Integer countByReviewId(int reviewId);
    Integer countReviewLikeHistoryByReviewId(int reviewId);
}
