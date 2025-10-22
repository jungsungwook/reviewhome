package com.memeki.reviewhome.community.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecentPostResponseDto {
    private List<PostItemDto> buildingPosts;
    private List<PostItemDto> townPosts;
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class PostItemDto {
        private Long postId;
        private String title;
        private String content;
        private Long createdBy;
        private String createdAt;
        private Long likeCount;
        private Long viewCount;
        private Long replyCount;
        
        // 커뮤니티 정보
        private CommunityInfo community;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CommunityInfo {
        private String uuid;
        private String name;
        private String type;
        private String geoFeaturesName;
    }
}

