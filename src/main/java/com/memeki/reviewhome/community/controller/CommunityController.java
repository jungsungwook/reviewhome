package com.memeki.reviewhome.community.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.memeki.reviewhome.community.dto.CommunityDefaultResponse;
import com.memeki.reviewhome.community.dto.CommunityEnterRequest;
import com.memeki.reviewhome.community.dto.CommunityLeaveRequest;
import com.memeki.reviewhome.community.dto.CommunityPostDetailResponse;
import com.memeki.reviewhome.community.dto.CommunityPostSimple;
import com.memeki.reviewhome.community.dto.CommunityPostsResponse;
import com.memeki.reviewhome.community.dto.CommunityResponse;
import com.memeki.reviewhome.community.dto.CreatePostReplyRequest;
import com.memeki.reviewhome.community.dto.CreatePostRequest;
import com.memeki.reviewhome.community.entity.Community;
import com.memeki.reviewhome.community.service.CommunityService;
import com.memeki.reviewhome.global.security.oauth2.UserPrincipal;

import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(value = "/api/community")
public class CommunityController {
        @Autowired
        private CommunityService communityService;

        @GetMapping("/building")
        public ResponseEntity<CommunityDefaultResponse> getCommunityByCommunityUuid(
                        @RequestParam String uuid,
                        @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) throws Exception {
                CommunityDefaultResponse response = new CommunityDefaultResponse();
                response.setCommunity(
                                communityService.getCommunityByCommunityUuid(uuid,
                                                userDetails != null ? userDetails.getId() : null));
                try {
                        Pageable pageable = PageRequest.of(0, 5);
                        response.setPopularPosts(
                                        communityService.getPopularPosts(uuid, pageable,
                                                        userDetails != null ? userDetails.getId() : null));
                        response.setRecentPosts(
                                        communityService.getCommunityPosts(uuid, pageable,
                                                        userDetails != null ? userDetails.getId() : null));
                        response.setStatusCode(200);
                } catch (Exception e) {
                        response.setStatusCode(207);
                        return ResponseEntity.ok(response);
                }
                return ResponseEntity.ok(response);

        }

        @GetMapping("/building/post/detail")
        public ResponseEntity<CommunityPostDetailResponse> getCommunitiyPostDetailById(
                        @RequestParam Long id) {
                CommunityPostDetailResponse response = new CommunityPostDetailResponse();
                response.setCommunityPost(communityService.getCommunityPostById(id));
                CommunityPostSimple prevPost = communityService.getPrevPost(id);
                CommunityPostSimple nextPost = communityService.getNextPost(id);
                response.setPreviousPost(prevPost);
                response.setNextPost(nextPost);
                List<CommunityPostSimple> nearList = communityService.getNearPosts(id, 11);
                response.setNearPosts(nearList);
                response.setStatusCode(200);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/building/posts")
        public ResponseEntity<CommunityPostsResponse> getCommunityPostsByCommunityUuid(
                        @RequestParam String uuid,
                        @RequestParam(required = false) Integer page,
                        @RequestParam(required = false) Integer size,
                        @RequestParam(required = false) String search) {
                try {
                        Pageable pageable = PageRequest.of(page, size);
                        CommunityPostsResponse response = communityService.getCommunityPosts(uuid, pageable, search);
                        response.setStatusCode(200);
                        return ResponseEntity.ok(response);
                } catch (Exception e) {
                        e.printStackTrace();
                        CommunityPostsResponse response = new CommunityPostsResponse();
                        response.setStatusCode(207);
                        return ResponseEntity.ok(response);
                }
        }

        @PostMapping("/building/post")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<Void> createCommunityPost(
                        @RequestBody CreatePostRequest body,
                        @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) {
                body.setCreatedBy(userDetails.getId());
                communityService.createCommunityPost(body);
                return ResponseEntity.ok().build();
        }

        @PostMapping("/building/post/comment")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<Void> createCommunityPostComment(
                        @RequestBody CreatePostReplyRequest body,
                        @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) {
                body.setCreatedBy(userDetails.getId());
                communityService.createCommunityPostComment(body);
                return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        @GetMapping("/building/find")
        public ResponseEntity<CommunityResponse> getDefaultBuildingCommunity(
                        @RequestParam String uuid,
                        @RequestParam(required = false) String type2,
                        @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) throws Exception {
                CommunityResponse response = new CommunityResponse();
                if (type2 == null) {
                        type2 = "default";
                }
                response.setCommunity(
                                communityService.getBuildingCommunity(uuid, type2,
                                                userDetails != null ? userDetails.getId() : null));
                response.setStatusCode(200);
                return ResponseEntity.ok(response);
        }

        // @GetMapping("/building/list")
        // public ResponseEntity<CommunityResponse> getCommunityByBuildingId(
        // @RequestParam String uuid,
        // @RequestParam(required = false) String type2,
        // @AuthenticationPrincipal UserPrincipal userDetails) throws Exception {

        // }

        @PostMapping("/building/enter")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<CommunityResponse> getMethodName(
                        @RequestBody CommunityEnterRequest body,
                        @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) {
                CommunityResponse response = new CommunityResponse();
                body.setUserId(userDetails.getId());
                Community community = communityService.enterCommunity(body);
                response.setCommunity(community);
                response.setStatusCode(200);
                return ResponseEntity.ok(response);
        }

        @PostMapping("/building/leave")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<CommunityResponse> leaveCommunity(
                        @RequestBody CommunityLeaveRequest body,
                        @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) {
                CommunityResponse response = new CommunityResponse();
                body.setUserId(userDetails.getId());
                communityService.leaveCommunity(body);
                response.setStatusCode(200);
                return ResponseEntity.ok(response);
        }
}
