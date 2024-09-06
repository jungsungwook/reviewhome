package com.memeki.reviewhome.review.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.memeki.reviewhome.review.entity.BuildingReviewContent;
import com.memeki.reviewhome.review.entity.Review;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class BuildingReview {
    Review review;
    BuildingReviewContent buildingReviewContent;
}
