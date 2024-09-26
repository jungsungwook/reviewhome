package com.memeki.reviewhome.review.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.memeki.reviewhome.review.dto.TownReviewCreateDto;
import com.memeki.reviewhome.review.entity.TownReviewContent;
import com.memeki.reviewhome.review.repository.TownReviewContentRepository;
import com.memeki.reviewhome.review.repository.TownReviewRepository;
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

import com.memeki.reviewhome.review.dto.TownReviewResponseDto;


@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TownReviewRepository townReviewRepository;//town

    @Autowired
    private BuildingReviewContentRepository buildingReviewContentRepository;

    @Autowired
    private ReviewLikeHistoryRepository reviewLikeHistoryRepository;

    @Autowired
    private TownReviewContentRepository townReviewContentRepository;//town



    public ReviewService(ReviewRepository reviewRepository,TownReviewRepository townReviewRepository) {
        this.reviewRepository = reviewRepository;
        this.townReviewRepository = townReviewRepository; //town
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

    //town
    @Transactional
    public String towncreateReview(TownReviewCreateDto townReviewCreateDto) {
        if(townReviewCreateDto.getContent()==null){
            throw new IllegalArgumentException("공백을 입력하시면 안됩니다.");
        }
        if(townReviewCreateDto.getUserId() == null){
            throw new IllegalArgumentException("userId가 null입니다.");
        }
        try {
            Review townreview = new Review();
            townreview.setType("town");
            townreview.setTargetId(townReviewCreateDto.getTargetId());
            townreview.setCreatedBy(townReviewCreateDto.getUserId());
//            townreview.setContent(townReviewCreateDto.getContent());
//            townreview.setTitle(townReviewCreateDto.getTitle());
            townReviewRepository.save(townreview);

            TownReviewContent townReviewContent = new TownReviewContent();
            townReviewContent.setReviewId(townreview.getId());
            townReviewContent.setTitle(townReviewCreateDto.getTitle());
            townReviewContent.setContent(townReviewCreateDto.getContent());
            townReviewContent.setReviewType(townReviewCreateDto.getType());
            townReviewContentRepository.save(townReviewContent);

            return "리뷰 생성에 성공하였습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("리뷰등록에 실패하였습니다.");
        }

    }



}