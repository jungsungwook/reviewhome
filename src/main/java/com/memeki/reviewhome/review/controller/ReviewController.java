package com.memeki.reviewhome.review.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.memeki.reviewhome.review.dto.ReviewCreateDto;
import com.memeki.reviewhome.review.dto.ReviewResponseDto;
import com.memeki.reviewhome.review.entity.Review;
import com.memeki.reviewhome.review.service.ReviewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(value = "/api/review")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @GetMapping("")
    public ResponseEntity<ReviewResponseDto> getReviewByReviewId(@RequestParam int reviewId) throws Exception{
        Review result = reviewService.getReviewByReviewId(reviewId);
        ReviewResponseDto response = new ReviewResponseDto();
        response.setReview(result);
        response.setStatusCode(200);
        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponseDto> createReview(
        @RequestBody ReviewCreateDto body,
        @AuthenticationPrincipal UserDetails userDetails
    ) throws Exception {
        reviewService.createReview(body.toString());
        return null;
    }
    
}
