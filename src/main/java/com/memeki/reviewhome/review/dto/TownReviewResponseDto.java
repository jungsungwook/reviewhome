package com.memeki.reviewhome.review.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.memeki.reviewhome.review.entity.Review;
import com.memeki.reviewhome.review.entity.TownReviewContent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TownReviewResponseDto {
    private int statusCode;
    private List<Review> townReview;
    private TownReviewContent townReviewContent;
}
