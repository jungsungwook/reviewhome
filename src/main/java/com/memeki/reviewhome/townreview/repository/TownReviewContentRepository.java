package com.memeki.reviewhome.townreview.repository;

import com.memeki.reviewhome.townreview.entity.TownReviewContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TownReviewContentRepository extends JpaRepository<TownReviewContent, Integer> {

    TownReviewContent findTownReviewContentById(int id);

}