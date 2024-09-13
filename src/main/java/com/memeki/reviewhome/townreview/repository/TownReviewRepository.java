package com.memeki.reviewhome.townreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.townreview.entity.TownReview;

import java.util.List;
public interface TownReviewRepository extends JpaRepository<TownReview, Integer> {
    TownReview findReviewById(int id);
    TownReview findReviewByTypeAndTargetId(String type, String targetId);
    List<TownReview> findReviewByType(String type);

    List<TownReview> findTownReviewByTypeAndCreatedBy(String type, Long createdBy);
}

