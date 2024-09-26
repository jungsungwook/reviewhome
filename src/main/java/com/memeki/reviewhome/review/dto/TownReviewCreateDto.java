package com.memeki.reviewhome.review.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class TownReviewCreateDto {
    private String type;
    private String targetId;
    private String createdBy;
    private String content;
    private Long userId;
    private String title;
}
