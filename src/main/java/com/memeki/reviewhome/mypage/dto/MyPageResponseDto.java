package com.memeki.reviewhome.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(description = "마이페이지 응답 DTO")
public class MyPageResponseDto {
    
    @Schema(description = "사용자 ID", example = "9")
    private Long userId;
    
    @Schema(description = "이메일", example = "user@example.com")
    private String email;
    
    @Schema(description = "이름", example = "홍길동")
    private String name;
    
    @Schema(description = "닉네임", example = "user_abc123")
    private String nickname;
    
    @Schema(description = "가입일", example = "2023-09-27T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "가입 플랫폼", example = "KAKAO")
    private String authProvider;
    
    @Schema(description = "가입한 커뮤니티 목록")
    private List<CommunityInfoDto> communities;
    
    @Schema(description = "작성한 글 목록")
    private List<PostInfoDto> posts;
    
    @Schema(description = "작성한 댓글 목록")
    private List<ReplyInfoDto> replies;
    
    @Schema(description = "작성한 리뷰 목록")
    private List<ReviewInfoDto> reviews;
    
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @Schema(description = "커뮤니티 정보")
    public static class CommunityInfoDto {
        @Schema(description = "커뮤니티 UUID", example = "abc123def456")
        private String communityUuid;
        
        @Schema(description = "커뮤니티 이름", example = "강남구 아파트 커뮤니티")
        private String communityName;
        
        @Schema(description = "커뮤니티 타입", example = "building")
        private String type;
        
        @Schema(description = "가입일", example = "2023-09-27T10:30:00")
        private LocalDateTime joinedAt;
        
        @Schema(description = "가입 시 닉네임", example = "user_abc123")
        private String joinedNickname;
    }
    
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @Schema(description = "게시글 정보")
    public static class PostInfoDto {
        @Schema(description = "게시글 ID", example = "1")
        private Long postId;
        
        @Schema(description = "제목", example = "안녕하세요")
        private String title;
        
        @Schema(description = "내용", example = "첫 게시글입니다.")
        private String content;
        
        @Schema(description = "커뮤니티 UUID", example = "abc123def456")
        private String communityUuid;
        
        @Schema(description = "커뮤니티 이름", example = "강남구 아파트 커뮤니티")
        private String communityName;
        
        @Schema(description = "작성일", example = "2023-09-27T10:30:00")
        private LocalDateTime createdAt;
        
        @Schema(description = "좋아요 수", example = "5")
        private int likeCount;
        
        @Schema(description = "조회수", example = "10")
        private int viewCount;
        
        @Schema(description = "댓글 수", example = "3")
        private int replyCount;
    }
    
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @Schema(description = "댓글 정보")
    public static class ReplyInfoDto {
        @Schema(description = "댓글 ID", example = "1")
        private Long replyId;
        
        @Schema(description = "게시글 ID", example = "1")
        private Long postId;
        
        @Schema(description = "게시글 제목", example = "안녕하세요")
        private String postTitle;
        
        @Schema(description = "댓글 내용", example = "좋은 글 감사합니다.")
        private String content;
        
        @Schema(description = "커뮤니티 UUID", example = "abc123def456")
        private String communityUuid;
        
        @Schema(description = "커뮤니티 이름", example = "강남구 아파트 커뮤니티")
        private String communityName;
        
        @Schema(description = "작성일", example = "2023-09-27T10:30:00")
        private LocalDateTime createdAt;
        
        @Schema(description = "대댓글 여부", example = "false")
        private Boolean isReply;
    }
    
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @Schema(description = "리뷰 정보")
    public static class ReviewInfoDto {
        @Schema(description = "리뷰 ID", example = "1")
        private Integer reviewId;
        
        @Schema(description = "리뷰 타입", example = "building")
        private String type;
        
        @Schema(description = "대상 ID", example = "123")
        private String targetId;
        
        @Schema(description = "리뷰 내용", example = "좋은 건물입니다.")
        private String content;
        
        @Schema(description = "작성일", example = "2023-09-27T10:30:00")
        private LocalDateTime createdAt;
        
        @Schema(description = "좋아요 수", example = "3")
        private int likeCount;
    }
}
