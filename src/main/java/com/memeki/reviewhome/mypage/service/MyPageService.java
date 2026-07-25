package com.memeki.reviewhome.mypage.service;

import com.memeki.reviewhome.community.entity.Community;
import com.memeki.reviewhome.community.entity.CommunityEnterHistory;
import com.memeki.reviewhome.community.entity.CommunityPost;
import com.memeki.reviewhome.community.entity.CommunityPostReply;
import com.memeki.reviewhome.community.repository.CommunityEnterHistoryRepository;
import com.memeki.reviewhome.community.repository.CommunityPostRepository;
import com.memeki.reviewhome.community.repository.CommunityPostReplyRepository;
import com.memeki.reviewhome.community.repository.CommunityRepository;
import com.memeki.reviewhome.global.exception.DefaultException;
import com.memeki.reviewhome.global.exceptionHandler.ErrorCode;
import com.memeki.reviewhome.global.security.entity.User;
import com.memeki.reviewhome.global.security.repository.UserRepository;
import com.memeki.reviewhome.mypage.dto.MyPageResponseDto;
import com.memeki.reviewhome.mypage.dto.UpdateNicknameRequestDto;
import com.memeki.reviewhome.mypage.dto.UpdateNicknameResponseDto;
import com.memeki.reviewhome.review.entity.Review;
import com.memeki.reviewhome.review.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MyPageService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CommunityEnterHistoryRepository communityEnterHistoryRepository;
    
    @Autowired
    private CommunityRepository communityRepository;
    
    @Autowired
    private CommunityPostRepository communityPostRepository;
    
    @Autowired
    private CommunityPostReplyRepository communityPostReplyRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    /**
     * 마이페이지 정보 조회
     */
    public MyPageResponseDto getMyPageInfo(Long userId) {
        try {
            // 사용자 정보 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND));
            
            // 가입한 커뮤니티 조회
            List<CommunityEnterHistory> enterHistories = communityEnterHistoryRepository
                    .findByUserIdOrderByCreatedAtDesc(userId);
            
            // 작성한 글 조회
            List<CommunityPost> posts = communityPostRepository
                    .findByCreatedByOrderByCreatedAtDesc(userId);
            
            // 작성한 댓글 조회
            List<CommunityPostReply> replies = communityPostReplyRepository
                    .findByCreatedByOrderByCreatedAtDesc(userId);
            
            // 작성한 리뷰 조회
            List<Review> reviews = reviewRepository
                    .findByCreatedByOrderByCreatedAtDesc(userId);
            
            // DTO 변환
            MyPageResponseDto response = new MyPageResponseDto();
            response.setUserId(user.getId());
            response.setEmail(user.getEmail());
            response.setName(user.getName());
            response.setNickname(user.getNickname());
            response.setCreatedAt(user.getCreatedAt());
            response.setAuthProvider(user.getAuthProvider().name());
            
            // 커뮤니티 정보 변환
            List<MyPageResponseDto.CommunityInfoDto> communityInfos = enterHistories.stream()
                    .map(this::convertToCommunityInfoDto)
                    .collect(Collectors.toList());
            response.setCommunities(communityInfos);
            
            // 게시글 정보 변환
            List<MyPageResponseDto.PostInfoDto> postInfos = posts.stream()
                    .map(this::convertToPostInfoDto)
                    .collect(Collectors.toList());
            response.setPosts(postInfos);
            
            // 댓글 정보 변환
            List<MyPageResponseDto.ReplyInfoDto> replyInfos = replies.stream()
                    .map(this::convertToReplyInfoDto)
                    .collect(Collectors.toList());
            response.setReplies(replyInfos);
            
            // 리뷰 정보 변환
            List<MyPageResponseDto.ReviewInfoDto> reviewInfos = reviews.stream()
                    .map(this::convertToReviewInfoDto)
                    .collect(Collectors.toList());
            response.setReviews(reviewInfos);
            
            return response;
            
        } catch (Exception e) {
            log.error("마이페이지 정보 조회 중 오류 발생 - 사용자 ID: {}", userId, e);
            throw new DefaultException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 닉네임 수정
     */
    @Transactional
    public UpdateNicknameResponseDto updateNickname(Long userId, UpdateNicknameRequestDto request) {
        try {
            // 사용자 정보 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND));
            
            // 닉네임 중복 확인
            if (userRepository.findByNickname(request.getNickname()).isPresent()) {
                throw new DefaultException(ErrorCode.ALREADY_NICKNAME);
            }
            
            // 닉네임 업데이트
            user.setNickname(request.getNickname());
            userRepository.save(user);
            
            // 응답 생성
            UpdateNicknameResponseDto response = new UpdateNicknameResponseDto();
            response.setStatusCode(200);
            response.setMessage("닉네임이 성공적으로 변경되었습니다.");
            response.setNickname(user.getNickname());
            
            return response;
            
        } catch (DefaultException e) {
            throw e;
        } catch (Exception e) {
            log.error("닉네임 수정 중 오류 발생 - 사용자 ID: {}, 닉네임: {}", userId, request.getNickname(), e);
            throw new DefaultException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 커뮤니티 정보를 DTO로 변환
     */
    private MyPageResponseDto.CommunityInfoDto convertToCommunityInfoDto(CommunityEnterHistory history) {
        MyPageResponseDto.CommunityInfoDto dto = new MyPageResponseDto.CommunityInfoDto();
        dto.setCommunityUuid(history.getCommunityUuid());
        dto.setJoinedAt(history.getCreatedAt());
        dto.setJoinedNickname(history.getNickname());
        
        // 커뮤니티 상세 정보 조회
        try {
            Community community = communityRepository.findById(history.getCommunityUuid()).orElse(null);
            if (community != null) {
                dto.setCommunityName(community.getName());
                dto.setType(community.getType());
            }
        } catch (Exception e) {
            log.warn("커뮤니티 정보 조회 실패 - UUID: {}", history.getCommunityUuid(), e);
        }
        
        return dto;
    }
    
    /**
     * 게시글 정보를 DTO로 변환
     */
    private MyPageResponseDto.PostInfoDto convertToPostInfoDto(CommunityPost post) {
        MyPageResponseDto.PostInfoDto dto = new MyPageResponseDto.PostInfoDto();
        dto.setPostId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCommunityUuid(post.getCommunityUuid());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setLikeCount(post.getLikeCount());
        dto.setViewCount(post.getViewCount());
        dto.setReplyCount(post.getReplyCount());
        
        // 커뮤니티 이름 조회
        try {
            Community community = communityRepository.findById(post.getCommunityUuid()).orElse(null);
            if (community != null) {
                dto.setCommunityName(community.getName());
            }
        } catch (Exception e) {
            log.warn("커뮤니티 정보 조회 실패 - UUID: {}", post.getCommunityUuid(), e);
        }
        
        return dto;
    }
    
    /**
     * 댓글 정보를 DTO로 변환
     */
    private MyPageResponseDto.ReplyInfoDto convertToReplyInfoDto(CommunityPostReply reply) {
        MyPageResponseDto.ReplyInfoDto dto = new MyPageResponseDto.ReplyInfoDto();
        dto.setReplyId(reply.getId());
        dto.setPostId(reply.getPostId());
        dto.setContent(reply.getContent());
        dto.setCreatedAt(reply.getCreatedAt());
        dto.setIsReply(reply.getIsReply());
        
        // 게시글 제목과 커뮤니티 정보 조회
        try {
            CommunityPost post = communityPostRepository.findById(reply.getPostId()).orElse(null);
            if (post != null) {
                dto.setPostTitle(post.getTitle());
                dto.setCommunityUuid(post.getCommunityUuid());
                
                Community community = communityRepository.findById(post.getCommunityUuid()).orElse(null);
                if (community != null) {
                    dto.setCommunityName(community.getName());
                }
            }
        } catch (Exception e) {
            log.warn("게시글 또는 커뮤니티 정보 조회 실패 - 게시글 ID: {}", reply.getPostId(), e);
        }
        
        return dto;
    }
    
    /**
     * 리뷰 정보를 DTO로 변환
     */
    private MyPageResponseDto.ReviewInfoDto convertToReviewInfoDto(Review review) {
        MyPageResponseDto.ReviewInfoDto dto = new MyPageResponseDto.ReviewInfoDto();
        dto.setReviewId(review.getId());
        dto.setType(review.getType());
        dto.setTargetId(review.getTargetId());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setLikeCount(review.getLikeCount());
        
        // 리뷰 내용은 별도 테이블에 있을 수 있으므로 여기서는 기본값 설정
        dto.setContent("리뷰 내용"); // 실제로는 리뷰 내용 테이블에서 조회해야 함
        
        return dto;
    }
}
