package com.memeki.reviewhome.community.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CommunityAdminDto {
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class AdminRequest {
        private String communityUuid;
        private String requestMessage;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class AdminRequestResponse {
        private int id;
        private String communityUuid;
        private long userId;
        private String nickname;
        private String status;
        private String requestMessage;
        private String responseMessage;
        private Long processedBy;
        private String processedAt;
        private String createdAt;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ProcessRequest {
        private int requestId;
        private String communityUuid;
        private String status; // approved or rejected
        private String responseMessage;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class AdminInfo {
        private int id;
        private String communityUuid;
        private long userId;
        private String nickname;
        private String appointedAt;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class KickMemberRequest {
        private String communityUuid;
        private long targetUserId;
        private String reason;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class DeletePostRequest {
        private String communityUuid;
        private Long postId;
        private String reason;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class MemberInfo {
        private long userId;
        private String nickname;
        private String joinedAt;
        private int postCount;
        private int replyCount;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response {
        private int statusCode;
        private String message;
    }
}

