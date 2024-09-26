package com.memeki.reviewhome.review.repository;

import com.memeki.reviewhome.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TownReviewRepository extends JpaRepository<Review, Integer> {
    Review findReviewById(int id);

    Review findReviewByTypeAndTargetId(String type, String targetId);

    List<Review> findReviewByType(String type);

    List<Review> findTownReviewByTypeAndCreatedBy(String type, Long createdBy);
    boolean existsByTypeAndContentAndTargetId(String type, String content, String targetId);

    Optional<Review> findByTypeAndTargetIdAndIsDeletedFalse(String type, String targetId);
    Review findByTargetId(String targetId);
}

