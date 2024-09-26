package com.memeki.reviewhome.review.repository;

import com.memeki.reviewhome.review.entity.TownReviewContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TownReviewContentRepository extends JpaRepository<TownReviewContent, Integer> {

    TownReviewContent findTownReviewContentById(int id);

}