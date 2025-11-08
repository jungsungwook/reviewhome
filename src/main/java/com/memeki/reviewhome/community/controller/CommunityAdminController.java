package com.memeki.reviewhome.community.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.memeki.reviewhome.community.dto.CommunityAdminDto;
import com.memeki.reviewhome.community.dto.CommunityNoticeDto;
import com.memeki.reviewhome.community.entity.CommunityNotice;
import com.memeki.reviewhome.community.service.CommunityAdminService;
import com.memeki.reviewhome.community.service.CommunityNoticeService;
import com.memeki.reviewhome.global.exception.DefaultException;
import com.memeki.reviewhome.global.exceptionHandler.ErrorCode;
import com.memeki.reviewhome.global.security.oauth2.UserPrincipal;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/api/community/admin")
@Tag(name = "Community Admin", description = "커뮤니티 관리자 API")
public class CommunityAdminController {

    @Autowired
    private CommunityAdminService communityAdminService;

    @Autowired
    private CommunityNoticeService communityNoticeService;

    // ==================== 관리자 신청 관련 ====================

    @PostMapping("/request")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommunityAdminDto.Response> requestAdmin(
            @RequestBody CommunityAdminDto.AdminRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) throws Exception {
        communityAdminService.requestAdmin(request.getCommunityUuid(), 
                userDetails.getId(), request.getRequestMessage());
        
        CommunityAdminDto.Response response = new CommunityAdminDto.Response();
        response.setStatusCode(200);
        response.setMessage("관리자 신청이 완료되었습니다");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/requests")
    public ResponseEntity<List<CommunityAdminDto.AdminRequestResponse>> getAdminRequests(
            @RequestParam String communityUuid,
            @RequestParam(required = false) String status) {
        List<CommunityAdminDto.AdminRequestResponse> requests = 
                communityAdminService.getAdminRequests(communityUuid, status);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/process")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommunityAdminDto.Response> processAdminRequest(
            @RequestBody CommunityAdminDto.ProcessRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) throws Exception {
        
        // 관리자 권한 확인
        if (!communityAdminService.isAdmin(request.getCommunityUuid(), userDetails.getId())) {
            throw new DefaultException(ErrorCode.FORBIDDEN);
        }

        communityAdminService.processAdminRequest(
                request.getRequestId(),
                request.getCommunityUuid(),
                userDetails.getId(),
                request.getStatus(),
                request.getResponseMessage()
        );

        CommunityAdminDto.Response response = new CommunityAdminDto.Response();
        response.setStatusCode(200);
        response.setMessage("처리가 완료되었습니다");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<CommunityAdminDto.AdminInfo>> getAdmins(
            @RequestParam String communityUuid) {
        List<CommunityAdminDto.AdminInfo> admins = communityAdminService.getAdmins(communityUuid);
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/check")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> checkAdmin(
            @RequestParam String communityUuid,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) {
        boolean isAdmin = communityAdminService.isAdmin(communityUuid, userDetails.getId());
        return ResponseEntity.ok(isAdmin);
    }

    @GetMapping("/has-admin")
    public ResponseEntity<Boolean> hasAdmin(@RequestParam String communityUuid) {
        boolean hasAdmin = communityAdminService.hasAdmin(communityUuid);
        return ResponseEntity.ok(hasAdmin);
    }

    // ==================== 회원 관리 ====================

    @GetMapping("/members")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CommunityAdminDto.MemberInfo>> getMembers(
            @RequestParam String communityUuid,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) throws Exception {
        
        // 관리자 권한 확인
        if (!communityAdminService.isAdmin(communityUuid, userDetails.getId())) {
            throw new DefaultException(ErrorCode.FORBIDDEN);
        }

        List<CommunityAdminDto.MemberInfo> members = communityAdminService.getCommunityMembers(communityUuid);
        return ResponseEntity.ok(members);
    }

    @PostMapping("/kick")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommunityAdminDto.Response> kickMember(
            @RequestBody CommunityAdminDto.KickMemberRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) throws Exception {
        
        // 관리자 권한 확인
        if (!communityAdminService.isAdmin(request.getCommunityUuid(), userDetails.getId())) {
            throw new DefaultException(ErrorCode.FORBIDDEN);
        }

        communityAdminService.kickMember(request.getCommunityUuid(), 
                request.getTargetUserId(), userDetails.getId());

        CommunityAdminDto.Response response = new CommunityAdminDto.Response();
        response.setStatusCode(200);
        response.setMessage("회원이 추방되었습니다");
        return ResponseEntity.ok(response);
    }

    // ==================== 게시글 관리 ====================

    @DeleteMapping("/post")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommunityAdminDto.Response> deletePost(
            @RequestBody CommunityAdminDto.DeletePostRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) throws Exception {
        
        // 관리자 권한 확인
        if (!communityAdminService.isAdmin(request.getCommunityUuid(), userDetails.getId())) {
            throw new DefaultException(ErrorCode.FORBIDDEN);
        }

        communityAdminService.deletePost(request.getPostId(), request.getCommunityUuid());

        CommunityAdminDto.Response response = new CommunityAdminDto.Response();
        response.setStatusCode(200);
        response.setMessage("게시글이 삭제되었습니다");
        return ResponseEntity.ok(response);
    }

    // ==================== 공지사항 관리 ====================

    @PostMapping("/notice")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommunityNoticeDto.Response> createNotice(
            @RequestBody CommunityNoticeDto.CreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) throws Exception {
        
        // 관리자 권한 확인
        if (!communityAdminService.isAdmin(request.getCommunityUuid(), userDetails.getId())) {
            throw new DefaultException(ErrorCode.FORBIDDEN);
        }

        CommunityNotice notice = communityNoticeService.createNotice(
                request.getCommunityUuid(),
                request.getTitle(),
                request.getContent(),
                request.getIsPinned(),
                userDetails.getId()
        );

        CommunityNoticeDto.Response response = new CommunityNoticeDto.Response();
        response.setStatusCode(200);
        response.setMessage("공지사항이 작성되었습니다");
        response.setNotice(communityNoticeService.getNotice(notice.getId(), request.getCommunityUuid()));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/notice")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommunityNoticeDto.Response> updateNotice(
            @RequestBody CommunityNoticeDto.UpdateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) throws Exception {
        
        // 관리자 권한 확인
        if (!communityAdminService.isAdmin(request.getCommunityUuid(), userDetails.getId())) {
            throw new DefaultException(ErrorCode.FORBIDDEN);
        }

        CommunityNotice notice = communityNoticeService.updateNotice(
                request.getNoticeId(),
                request.getCommunityUuid(),
                request.getTitle(),
                request.getContent(),
                request.getIsPinned()
        );

        CommunityNoticeDto.Response response = new CommunityNoticeDto.Response();
        response.setStatusCode(200);
        response.setMessage("공지사항이 수정되었습니다");
        response.setNotice(communityNoticeService.getNotice(notice.getId(), request.getCommunityUuid()));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/notice")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommunityNoticeDto.Response> deleteNotice(
            @RequestBody CommunityNoticeDto.DeleteRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) throws Exception {
        
        // 관리자 권한 확인
        if (!communityAdminService.isAdmin(request.getCommunityUuid(), userDetails.getId())) {
            throw new DefaultException(ErrorCode.FORBIDDEN);
        }

        communityNoticeService.deleteNotice(request.getNoticeId(), request.getCommunityUuid());

        CommunityNoticeDto.Response response = new CommunityNoticeDto.Response();
        response.setStatusCode(200);
        response.setMessage("공지사항이 삭제되었습니다");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/notice/list")
    public ResponseEntity<CommunityNoticeDto.NoticeListResponse> getNotices(
            @RequestParam String communityUuid,
            @RequestParam(required = false) Integer limit) {
        List<CommunityNoticeDto.NoticeInfo> notices = 
                communityNoticeService.getNotices(communityUuid, limit);
        
        CommunityNoticeDto.NoticeListResponse response = new CommunityNoticeDto.NoticeListResponse();
        response.setNotices(notices);
        response.setStatusCode(200);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/notice/pinned")
    public ResponseEntity<CommunityNoticeDto.NoticeListResponse> getPinnedNotices(
            @RequestParam String communityUuid) {
        List<CommunityNoticeDto.NoticeInfo> notices = 
                communityNoticeService.getPinnedNotices(communityUuid);
        
        CommunityNoticeDto.NoticeListResponse response = new CommunityNoticeDto.NoticeListResponse();
        response.setNotices(notices);
        response.setStatusCode(200);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/notice")
    public ResponseEntity<CommunityNoticeDto.NoticeInfo> getNotice(
            @RequestParam Long noticeId,
            @RequestParam String communityUuid) throws Exception {
        CommunityNoticeDto.NoticeInfo notice = 
                communityNoticeService.getNotice(noticeId, communityUuid);
        return ResponseEntity.ok(notice);
    }
}

