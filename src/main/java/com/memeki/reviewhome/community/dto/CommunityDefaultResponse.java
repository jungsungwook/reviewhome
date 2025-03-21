package com.memeki.reviewhome.community.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.memeki.reviewhome.community.entity.Community;
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
public class CommunityDefaultResponse {
    private int statusCode;
    private Community community;
    private List<CommunityPost> recentPosts;
    private List<CommunityPost> popularPosts;
}
