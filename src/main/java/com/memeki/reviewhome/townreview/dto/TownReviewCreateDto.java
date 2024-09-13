package com.memeki.reviewhome.townreview.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TownReviewCreateDto {
    private String type;
    private String targetId;
    private String createdBy;
    private String content;
    private Long userId;
    private String title;
}
