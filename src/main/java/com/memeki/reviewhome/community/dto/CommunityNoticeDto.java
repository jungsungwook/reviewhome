package com.memeki.reviewhome.community.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class CommunityNoticeDto {
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateRequest {
        private String communityUuid;
        private String title;
        private String content;
        private Boolean isPinned;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateRequest {
        private Long noticeId;
        private String communityUuid;
        private String title;
        private String content;
        private Boolean isPinned;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class DeleteRequest {
        private Long noticeId;
        private String communityUuid;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class NoticeInfo {
        private Long id;
        private String communityUuid;
        private String title;
        private String content;
        private Boolean isPinned;
        private long createdBy;
        private String createdByNickname;
        private String createdAt;
        private String updatedAt;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class NoticeListResponse {
        private List<NoticeInfo> notices;
        private int statusCode;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response {
        private int statusCode;
        private String message;
        private NoticeInfo notice;
    }
}

