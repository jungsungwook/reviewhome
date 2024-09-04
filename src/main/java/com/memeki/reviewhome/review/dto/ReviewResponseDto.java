package com.memeki.reviewhome.review.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.memeki.reviewhome.review.entity.Review;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewResponseDto {
    private int statusCode;
    private Review review;
}
