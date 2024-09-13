package com.memeki.reviewhome.townreview.controller;
import java.util.List;
import java.util.Map;

import com.memeki.reviewhome.townreview.dto.TownReviewDto;
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
//import com.memeki.reviewhome.review.dto.BuildingReview;
//import com.memeki.reviewhome.review.dto.ReivewReplyDto;
//import com.memeki.reviewhome.review.dto.ReviewLikeDto;
import org.springframework.web.bind.annotation.PutMapping;
import com.memeki.reviewhome.townreview.dto.TownReviewCreateDto;
import com.memeki.reviewhome.townreview.dto.TownReviewResponseDto;
import com.memeki.reviewhome.townreview.entity.TownReview;
import com.memeki.reviewhome.townreview.service.TownReviewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(value = "/api/townreview")
public class TownReviewController {
    @Autowired
    private TownReviewService townReviewService;
    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<TownReviewResponseDto> getReviewByReviewId(@RequestParam int reviewId) throws Exception{
        TownReview result = townReviewService.getReviewByReviewId(reviewId);
        TownReviewResponseDto response = new TownReviewResponseDto();
        response.setTownReview(result);
        response.setStatusCode(200);
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/town")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<TownReviewResponseDto> towncreateReview(
//            @RequestBody TownReviewCreateDto body,
//            @AuthenticationPrincipal UserPrincipal userDetails) throws Exception {
//        User user = userService.getUserById(userDetails.getId());
//        body.setUserId(user.getId());
//        townReviewService.towncreateReview(body);
//
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/town")//로그인없이 리뷰를 작성하는 코드
    public String createTownReview(@RequestBody TownReviewCreateDto townReviewCreateDto) {
        return townReviewService.towncreateReview(townReviewCreateDto);
    }

    @GetMapping("/town")//
    public List<TownReview> findAllTownReviewsByTypeAndCreatedBy(@RequestParam String type, @RequestParam Long createdBy) {
        return townReviewService.findAllTownReviewsByTypeAndCreatedBy(type, createdBy);
    }

//    @GetMapping("/town")
//    public ResponseEntity<List<TownReviewResponseDto>> findReviewsByType(@RequestParam String type) {
//        List<TownReviewResponseDto> response = townReviewService.findReviewByType(type);
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/town/{targetId}")
    public ResponseEntity<TownReviewResponseDto> findReviewByTypeAndTargetId(@RequestParam String type, @RequestParam String targetId) {
        TownReviewResponseDto response = townReviewService.findReviewByTypeAndTargetId(type, targetId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/town/{targetId}")
    public ResponseEntity<Void> updateReviewContentByTypeAndUser(@RequestBody Map<String, String> body) {
        String type = body.get("type");
        String newContent = body.get("newContent");
        String targetId = body.get("targetId");
        townReviewService.updateReviewContentByTypeAndUser(type, newContent, targetId);
        return ResponseEntity.ok().build();
    }

}

