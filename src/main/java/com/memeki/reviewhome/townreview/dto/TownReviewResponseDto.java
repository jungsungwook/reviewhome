package com.memeki.reviewhome.townreview.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.memeki.reviewhome.townreview.entity.TownReview;

import com.memeki.reviewhome.townreview.entity.TownReviewContent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TownReviewResponseDto {
    private int statusCode;
    private TownReview townReview;
    private TownReviewContent townReviewContent;
}
