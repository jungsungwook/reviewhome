package com.memeki.reviewhome.review.dto;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.memeki.reviewhome.review.entity.Review;
import com.memeki.reviewhome.review.entity.TownReviewContent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class TownReviewDto {
    Review townReview;
    TownReviewContent townReviewContent;
}
