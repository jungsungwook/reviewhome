package com.memeki.reviewhome.community.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.memeki.reviewhome.community.entity.CommunityPost;

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
    private int replyCount;

    public CommunityPostSimple(CommunityPost post) {
        this.id = post.getId();
        this.postType = post.getPostType();
        this.communityUuid = post.getCommunityUuid();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.createdBy = post.getCreatedBy();
        this.likeCount = post.getLikeCount();
        this.viewCount = post.getViewCount();
        this.replyCount = post.getReplyCount();
    }
}
