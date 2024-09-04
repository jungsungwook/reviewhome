package com.memeki.reviewhome.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Review findReviewById(int id);

    Review findReviewByTypeAndTargetId(String type, String targetId);
}