package com.memeki.reviewhome.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.review.entity.BuildingReviewContent;

public interface BuildingReviewContentRepository extends JpaRepository<BuildingReviewContent, Integer> {
    BuildingReviewContent findBuildingReviewContentById(int id);

    BuildingReviewContent findBuildingReviewContentByReviewId(int reviewId);

    BuildingReviewContent findBuildingReviewContentByReviewTypeAndReviewId(String type, int reviewId);

    List<BuildingReviewContent> findAllByReviewTypeAndReviewId(String type, int reviewId);
    
}
