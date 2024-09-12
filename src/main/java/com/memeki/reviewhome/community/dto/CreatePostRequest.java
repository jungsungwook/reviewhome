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
public class CreatePostRequest {
    private String communityUuid;
    private String title;
    private String content;
    private String postType;
    private long createdBy;
}
