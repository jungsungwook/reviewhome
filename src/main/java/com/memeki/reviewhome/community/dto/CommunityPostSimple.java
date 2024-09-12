package com.memeki.reviewhome.community.dto;

import java.time.LocalDateTime;

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
public class CommunityPostSimple {
    private Long id;
    private String postType;
    private String communityUuid;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private long createdBy;
    private int likeCount;
    private int viewCount;
}
