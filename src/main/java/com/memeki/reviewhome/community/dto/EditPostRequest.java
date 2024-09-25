package com.memeki.reviewhome.community.dto;

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
public class EditPostRequest {
    private long postId;
    private String title;
    private String content;
    private String postType;
    private long createdBy;
}
