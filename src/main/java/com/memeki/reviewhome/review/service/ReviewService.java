package com.memeki.reviewhome.review.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memeki.reviewhome.global.exception.DefaultException;
import com.memeki.reviewhome.global.exceptionHandler.ErrorCode;
import com.memeki.reviewhome.review.dto.BuildingReview;
import com.memeki.reviewhome.review.dto.ReivewReplyDto;
import com.memeki.reviewhome.review.dto.ReviewCreateDto;
import com.memeki.reviewhome.review.entity.BuildingReviewContent;
import com.memeki.reviewhome.review.entity.Review;
import com.memeki.reviewhome.review.entity.ReviewLikeHistory;
import com.memeki.reviewhome.review.repository.BuildingReviewContentRepository;
import com.memeki.reviewhome.review.repository.ReviewLikeHistoryRepository;
import com.memeki.reviewhome.review.repository.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BuildingReviewContentRepository buildingReviewContentRepository;

    @Autowired
    private ReviewLikeHistoryRepository reviewLikeHistoryRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review getReviewByReviewId(int reviewId) throws Exception {
        try {
            return reviewRepository.findReviewById(reviewId);
        } catch (Exception e) {
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }
    }

    @Transactional
    public String createReview(ReviewCreateDto dto) throws Exception {
        try {
            Review review = new Review();
            review.setTargetId(dto.getTargetId());
            review.setType("building");
            review.setCreatedBy(dto.getUserId());
            reviewRepository.save(review);

            BuildingReviewContent buildingReviewContent = new BuildingReviewContent();
            buildingReviewContent.setReviewId(review.getId());
            buildingReviewContent.setTitle(dto.getTitle());
            buildingReviewContent.setContent(dto.getContent());
            buildingReviewContent.setReviewType(dto.getReviewType());
            buildingReviewContent.setRating(dto.getRating());
            buildingReviewContentRepository.save(buildingReviewContent);

            return "";
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Review not created");
        }
    }

    @Transactional
    public int likeReview(int reviewId, long userId) throws Exception {
        try {
            ReviewLikeHistory reviewLikeHistory = reviewLikeHistoryRepository.findReviewLikeHistoryByUserIdAndReviewId(
                    userId, reviewId);
            if (reviewLikeHistory != null) {
                if(reviewLikeHistory.getIsDeleted()){
                    reviewLikeHistory.setIsDeleted(false);
                    reviewLikeHistoryRepository.save(reviewLikeHistory);
                    return 1;

                } else {
                    reviewLikeHistory.setIsDeleted(true);
                    reviewLikeHistory.setDeletedAt(LocalDateTime.now());
                    reviewLikeHistoryRepository.save(reviewLikeHistory);
                    return 0;
                }
            }

            reviewLikeHistory = new ReviewLikeHistory();
            reviewLikeHistory.setReviewId(reviewId);
            reviewLikeHistory.setUserId(userId);
            reviewLikeHistory.setCreatedBy(userId);
            reviewLikeHistoryRepository.save(reviewLikeHistory);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }
    }

    @Transactional
    public List<BuildingReview> getAllReviewByBuildingId(String uuid, Long userId) throws Exception {
        try {
            List<Review> reviews = reviewRepository.findAllByTypeAndTargetId("building", uuid);
            List<BuildingReview> buildingReviews = new ArrayList<>(reviews.stream().map(review -> {
                BuildingReview buildingReview = new BuildingReview();
                review.setLikeCount(
                        reviewLikeHistoryRepository.countReviewLikeHistoryByReviewId(review.getId()));
                buildingReview.setReview(review);
                BuildingReviewContent buildingReviewContent = buildingReviewContentRepository
                        .findBuildingReviewContentByReviewId(review.getId());
                buildingReview.setBuildingReviewContent(buildingReviewContent);
                if (userId == null) {
                    buildingReview.setAlreadyLiked(false);
                } else {
                    Boolean alreadyLiked = reviewLikeHistoryRepository.findReviewLikeHistoryByUserIdAndReviewId(userId,
                            review.getId()) != null;
                    buildingReview.setAlreadyLiked(alreadyLiked);
                }
                return buildingReview;
            }).collect(Collectors.toList()));
            return buildingReviews;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }
    }

    @Transactional
    public void createReviewReply(ReivewReplyDto.Request dto) throws Exception {
        try {
            Review review = new Review();
            review.setTargetId(dto.getUuid());
            review.setReplyId(dto.getReviewId());
            review.setType("building");
            review.setCreatedBy(dto.getUserId());
            reviewRepository.save(review);

            BuildingReviewContent buildingReviewContent = new BuildingReviewContent();
            buildingReviewContent.setReviewId(review.getId());
            buildingReviewContent.setContent(dto.getContent());
            buildingReviewContent.setReviewType("reply");
            buildingReviewContentRepository.save(buildingReviewContent);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Review not created");
        }
    }
    
    /**
     * 타운 커뮤니티 리뷰 생성 (빌딩 리뷰와 동일한 로직, type만 "town"으로 설정)
     */
    @Transactional
    public String createTownReview(ReviewCreateDto dto) throws Exception {
        try {
            Review review = new Review();
            review.setTargetId(dto.getTargetId());
            review.setType("town");  // town 타입으로 설정
            review.setCreatedBy(dto.getUserId());
            reviewRepository.save(review);

            BuildingReviewContent buildingReviewContent = new BuildingReviewContent();
            buildingReviewContent.setReviewId(review.getId());
            buildingReviewContent.setTitle(dto.getTitle());
            buildingReviewContent.setContent(dto.getContent());
            buildingReviewContent.setReviewType(dto.getReviewType());
            buildingReviewContent.setRating(dto.getRating());
            buildingReviewContentRepository.save(buildingReviewContent);

            return "";
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Review not created");
        }
    }
    
    /**
     * 타운 커뮤니티 전체 리뷰 조회 (빌딩 리뷰와 동일한 로직, type만 "town"으로 조회)
     */
    @Transactional
    public List<BuildingReview> getAllReviewByTownId(String emdCd, Long userId) throws Exception {
        try {
            List<Review> reviews = reviewRepository.findAllByTypeAndTargetId("town", emdCd);
            List<BuildingReview> buildingReviews = new ArrayList<>(reviews.stream().map(review -> {
                BuildingReview buildingReview = new BuildingReview();
                review.setLikeCount(
                        reviewLikeHistoryRepository.countReviewLikeHistoryByReviewId(review.getId()));
                buildingReview.setReview(review);
                BuildingReviewContent buildingReviewContent = buildingReviewContentRepository
                        .findBuildingReviewContentByReviewId(review.getId());
                buildingReview.setBuildingReviewContent(buildingReviewContent);
                if (userId == null) {
                    buildingReview.setAlreadyLiked(false);
                } else {
                    Boolean alreadyLiked = reviewLikeHistoryRepository.findReviewLikeHistoryByUserIdAndReviewId(userId,
                            review.getId()) != null;
                    buildingReview.setAlreadyLiked(alreadyLiked);
                }
                return buildingReview;
            }).collect(Collectors.toList()));
            return buildingReviews;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }
    }
    
    /**
     * 타운 커뮤니티 리뷰 답글 생성 (빌딩 리뷰와 동일한 로직, type만 "town"으로 설정)
     */
    @Transactional
    public void createTownReviewReply(ReivewReplyDto.Request dto) throws Exception {
        try {
            Review review = new Review();
            review.setTargetId(dto.getUuid());
            review.setReplyId(dto.getReviewId());
            review.setType("town");  // town 타입으로 설정
            review.setCreatedBy(dto.getUserId());
            reviewRepository.save(review);

            BuildingReviewContent buildingReviewContent = new BuildingReviewContent();
            buildingReviewContent.setReviewId(review.getId());
            buildingReviewContent.setContent(dto.getContent());
            buildingReviewContent.setReviewType("reply");
            buildingReviewContentRepository.save(buildingReviewContent);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Review not created");
        }
    }
}