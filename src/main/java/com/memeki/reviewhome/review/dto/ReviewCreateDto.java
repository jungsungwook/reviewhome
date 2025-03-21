package com.memeki.reviewhome.review.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString()
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewCreateDto {
    String targetId;
    Long userId;
    String reviewType;
    String content;
    String title;
    int rating;
}
