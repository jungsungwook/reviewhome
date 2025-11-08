package com.memeki.reviewhome.community.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memeki.reviewhome.community.dto.CommunityAdminDto;
import com.memeki.reviewhome.community.dto.CommunityNoticeDto;
import com.memeki.reviewhome.community.entity.*;
import com.memeki.reviewhome.community.repository.*;
import com.memeki.reviewhome.global.exception.DefaultException;
import com.memeki.reviewhome.global.exceptionHandler.ErrorCode;
import com.memeki.reviewhome.global.security.entity.User;
import com.memeki.reviewhome.global.security.repository.UserRepository;

@Service
public class CommunityAdminService {

    @Autowired
    private CommunityAdminRepository communityAdminRepository;

    @Autowired
    private CommunityAdminRequestRepository adminRequestRepository;

    @Autowired
    private CommunityNoticeRepository communityNoticeRepository;

    @Autowired
    private CommunityEnterHistoryRepository communityEnterHistoryRepository;

    @Autowired
    private CommunityPostRepository communityPostRepository;

    @Autowired
    private CommunityPostReplyRepository communityPostReplyRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 관리자 신청
     */
    @Transactional
    public void requestAdmin(String communityUuid, long userId, String requestMessage) throws Exception {
        // 이미 신청한 내역이 있는지 확인 (pending 상태)
        if (adminRequestRepository.existsByCommunityUuidAndUserIdAndStatus(communityUuid, userId, "pending")) {
            throw new DefaultException(ErrorCode.ALREADY_EXIST);
        }

        // 이미 관리자인지 확인
        if (communityAdminRepository.existsByCommunityUuidAndUserId(communityUuid, userId)) {
            throw new DefaultException(ErrorCode.ALREADY_EXIST);
        }

        // 커뮤니티 가입 여부 확인
        if (!communityEnterHistoryRepository.existsCommunityEnterHistoryByUserIdAndCommunityUuid(userId, communityUuid)) {
            throw new DefaultException(ErrorCode.FORBIDDEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND));

        CommunityAdminRequest request = new CommunityAdminRequest();
        request.setCommunityUuid(communityUuid);
        request.setUserId(userId);
        request.setNickname(user.getNickname());
        request.setRequestMessage(requestMessage);
        request.setStatus("pending");

        adminRequestRepository.save(request);
    }

    /**
     * 관리자 신청 목록 조회
     */
    public List<CommunityAdminDto.AdminRequestResponse> getAdminRequests(String communityUuid, String status) {
        List<CommunityAdminRequest> requests;
        if (status != null && !status.isEmpty()) {
            requests = adminRequestRepository.findAllByCommunityUuidAndStatus(communityUuid, status);
        } else {
            requests = adminRequestRepository.findAllByCommunityUuid(communityUuid);
        }

        return requests.stream().map(req -> {
            CommunityAdminDto.AdminRequestResponse dto = new CommunityAdminDto.AdminRequestResponse();
            dto.setId(req.getId());
            dto.setCommunityUuid(req.getCommunityUuid());
            dto.setUserId(req.getUserId());
            dto.setNickname(req.getNickname());
            dto.setStatus(req.getStatus());
            dto.setRequestMessage(req.getRequestMessage());
            dto.setResponseMessage(req.getResponseMessage());
            dto.setProcessedBy(req.getProcessedBy());
            dto.setProcessedAt(req.getProcessedAt() != null ? req.getProcessedAt().toString() : null);
            dto.setCreatedAt(req.getCreatedAt().toString());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 관리자 신청 처리 (승인/거절)
     */
    @Transactional
    public void processAdminRequest(int requestId, String communityUuid, long processedBy, 
                                    String status, String responseMessage) throws Exception {
        CommunityAdminRequest request = adminRequestRepository.findByIdAndCommunityUuid(requestId, communityUuid);
        if (request == null) {
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }

        if (!"pending".equals(request.getStatus())) {
            throw new DefaultException(ErrorCode.INVALID_PARAMETER);
        }

        request.setStatus(status);
        request.setResponseMessage(responseMessage);
        request.setProcessedBy(processedBy);
        request.setProcessedAt(LocalDateTime.now());
        adminRequestRepository.save(request);

        // 승인인 경우 관리자로 등록
        if ("approved".equals(status)) {
            CommunityAdmin admin = new CommunityAdmin();
            admin.setCommunityUuid(communityUuid);
            admin.setUserId(request.getUserId());
            admin.setNickname(request.getNickname());
            admin.setAppointedBy(processedBy);
            communityAdminRepository.save(admin);
        }
    }

    /**
     * 관리자 여부 확인
     */
    public boolean isAdmin(String communityUuid, long userId) {
        return communityAdminRepository.existsByCommunityUuidAndUserId(communityUuid, userId);
    }

    /**
     * 커뮤니티 관리자 목록 조회
     */
    public List<CommunityAdminDto.AdminInfo> getAdmins(String communityUuid) {
        List<CommunityAdmin> admins = communityAdminRepository.findAllByCommunityUuid(communityUuid);
        return admins.stream().map(admin -> {
            CommunityAdminDto.AdminInfo dto = new CommunityAdminDto.AdminInfo();
            dto.setId(admin.getId());
            dto.setCommunityUuid(admin.getCommunityUuid());
            dto.setUserId(admin.getUserId());
            dto.setNickname(admin.getNickname());
            dto.setAppointedAt(admin.getAppointedAt().toString());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 커뮤니티 회원 목록 조회 (관리자 전용)
     */
    public List<CommunityAdminDto.MemberInfo> getCommunityMembers(String communityUuid) {
        List<CommunityEnterHistory> histories = communityEnterHistoryRepository
                .findAllByCommunityUuid(communityUuid);

        return histories.stream().map(history -> {
            CommunityAdminDto.MemberInfo dto = new CommunityAdminDto.MemberInfo();
            dto.setUserId(history.getUserId());
            dto.setNickname(history.getNickname());
            dto.setJoinedAt(history.getCreatedAt().toString());

            // 게시글 수
            List<CommunityPost> posts = communityPostRepository.findByCreatedByOrderByCreatedAtDesc(history.getUserId());
            int postCount = (int) posts.stream()
                    .filter(post -> post.getCommunityUuid().equals(communityUuid))
                    .count();
            dto.setPostCount(postCount);

            // 댓글 수
            List<CommunityPostReply> replies = communityPostReplyRepository
                    .findByCreatedByOrderByCreatedAtDesc(history.getUserId());
            int replyCount = 0;
            for (CommunityPostReply reply : replies) {
                CommunityPost post = communityPostRepository.findById(reply.getPostId()).orElse(null);
                if (post != null && post.getCommunityUuid().equals(communityUuid)) {
                    replyCount++;
                }
            }
            dto.setReplyCount(replyCount);

            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 회원 추방 (관리자 전용)
     */
    @Transactional
    public void kickMember(String communityUuid, long targetUserId, long adminId) throws Exception {
        // 관리자 본인은 추방 불가
        if (targetUserId == adminId) {
            throw new DefaultException(ErrorCode.INVALID_PARAMETER);
        }

        // 다른 관리자는 추방 불가
        if (communityAdminRepository.existsByCommunityUuidAndUserId(communityUuid, targetUserId)) {
            throw new DefaultException(ErrorCode.FORBIDDEN);
        }

        CommunityEnterHistory history = communityEnterHistoryRepository
                .findCommunityEnterHistoryByUserIdAndCommunityUuid(targetUserId, communityUuid);
        
        if (history == null) {
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }

        communityEnterHistoryRepository.delete(history);
    }

    /**
     * 게시글 삭제 (관리자 전용)
     */
    @Transactional
    public void deletePost(Long postId, String communityUuid) throws Exception {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND));

        if (!post.getCommunityUuid().equals(communityUuid)) {
            throw new DefaultException(ErrorCode.FORBIDDEN);
        }

        communityPostRepository.delete(post);
    }

    /**
     * 관리자 존재 여부 확인
     */
    public boolean hasAdmin(String communityUuid) {
        return communityAdminRepository.existsByCommunityUuid(communityUuid);
    }
}

