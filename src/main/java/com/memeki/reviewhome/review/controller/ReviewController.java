package com.memeki.reviewhome.review.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.memeki.reviewhome.review.dto.ReviewCreateDto;
import com.memeki.reviewhome.review.dto.ReviewResponseDto;
import com.memeki.reviewhome.review.entity.Review;
import com.memeki.reviewhome.review.service.ReviewService;

import java.util.List;
import java.util.Map;


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
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponseDto> createReview(
        @RequestBody ReviewCreateDto body,
        @AuthenticationPrincipal UserDetails userDetails
    ) throws Exception {
        //reviewService.createReview(body.toString());
        ReviewResponseDto response =reviewService.createReview(body);
        if(response==null){
            response = new ReviewResponseDto();
            response.setStatusCode(500);
            response.setReview(null);

        }
        return null;
    }

    @GetMapping("/Town find")
    public ResponseEntity<List<ReviewResponseDto>> findReviewsByType(@RequestParam String type) {
        List<ReviewResponseDto> response = reviewService.findReviewByType(type);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/typeAndTargetId")
    public ResponseEntity<ReviewResponseDto> findReviewByTypeAndTargetId(@RequestParam String type, @RequestParam String targetId) {
        ReviewResponseDto response = reviewService.findReviewByTypeAndTargetId(type, targetId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/updateContent")
    public ResponseEntity<Void> updateReviewContentByTypeAndUser(@RequestBody Map<String, String> body) {
        String type = body.get("type");
        String newContent = body.get("newContent");
        String targetId = body.get("targetId");
        reviewService.updateReviewContentByTypeAndUser(type, newContent, targetId);
        return ResponseEntity.ok().build();
    }
    
}
