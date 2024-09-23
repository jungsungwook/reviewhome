package com.memeki.reviewhome.community.dto;

import java.util.List;

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
public class CommunityPostDetailResponse {
    private int statusCode;
    private CommunityPost communityPost;
    private CommunityPostSimple previousPost;
    private CommunityPostSimple nextPost;
    private List<CommunityPostSimple> nearPosts;
}
