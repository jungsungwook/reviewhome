package com.memeki.reviewhome.review.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.memeki.reviewhome.global.security.entity.User;
import com.memeki.reviewhome.global.security.oauth2.UserPrincipal;
import com.memeki.reviewhome.global.security.service.UserService;
import com.memeki.reviewhome.review.dto.BuildingReview;
import com.memeki.reviewhome.review.dto.ReivewReplyDto;
import com.memeki.reviewhome.review.dto.ReviewCreateDto;
import com.memeki.reviewhome.review.dto.ReviewLikeDto;
import com.memeki.reviewhome.review.dto.ReviewResponseDto;
import com.memeki.reviewhome.review.entity.Review;
import com.memeki.reviewhome.review.service.ReviewService;

import com.memeki.reviewhome.review.dto.TownReviewResponseDto;

import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(value = "/api/review")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<ReviewResponseDto> getReviewByReviewId(@RequestParam int reviewId) throws Exception {
        Review result = reviewService.getReviewByReviewId(reviewId);
        ReviewResponseDto response = new ReviewResponseDto();
        // response.setReview(result);
        response.setStatusCode(200);
        return ResponseEntity.ok(response);
    }

    @PostMapping("like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewLikeDto.Response> likeReview(
            @RequestBody ReviewLikeDto.Request body,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) throws Exception {
        int status = reviewService.likeReview(body.getReviewId(), userDetails.getId());
        ReviewLikeDto.Response response = new ReviewLikeDto.Response();
        response.setStatusCode(200);
        response.setType(status);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reply")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReivewReplyDto.Response> createReviewReply(
            @RequestBody ReivewReplyDto.Request body,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) throws Exception {
        body.setUserId(userDetails.getId());
        reviewService.createReviewReply(body);
        ReivewReplyDto.Response response = new ReivewReplyDto.Response();
        response.setStatusCode(200);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/building")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponseDto> createReview(
            @RequestBody ReviewCreateDto body,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) throws Exception {
        User user = userService.getUserById(userDetails.getId());
        body.setUserId(user.getId());
        reviewService.createReview(body);
        return null;
    }

    @GetMapping("/building")
    public ResponseEntity<ReviewResponseDto> getReviewByBuildingId(
            @RequestParam String uuid,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) throws Exception {
        List<BuildingReview> result = reviewService.getAllReviewByBuildingId(uuid,
                userDetails != null ? userDetails.getId() : null);
        ReviewResponseDto response = new ReviewResponseDto();
        response.setBuildingReviews(result);
        response.setStatusCode(200);
        return ResponseEntity.ok(response);
    }
}
