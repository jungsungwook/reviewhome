package com.memeki.reviewhome.review.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memeki.reviewhome.global.exception.DefaultException;
import com.memeki.reviewhome.global.exceptionHandler.ErrorCode;
import com.memeki.reviewhome.review.dto.BuildingReview;
import com.memeki.reviewhome.review.dto.ReviewCreateDto;
import com.memeki.reviewhome.review.entity.BuildingReviewContent;
import com.memeki.reviewhome.review.entity.Review;
import com.memeki.reviewhome.review.repository.BuildingReviewContentRepository;
import com.memeki.reviewhome.review.repository.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BuildingReviewContentRepository buildingReviewContentRepository;

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
            review.setTargetId(dto.getTargeId());
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
    public List<BuildingReview> getAllReviewByBuildingId(String uuid) throws Exception {
        try {
            List<Review> reviews = reviewRepository.findAllByTypeAndTargetId("building", uuid);
            List<BuildingReview> buildingReviews = new ArrayList<>(reviews.stream().map(review -> {
                BuildingReview buildingReview = new BuildingReview();
                buildingReview.setReview(review);
                BuildingReviewContent buildingReviewContent = buildingReviewContentRepository
                        .findBuildingReviewContentByReviewId(review.getId());
                buildingReview.setBuildingReviewContent(buildingReviewContent);
                return buildingReview;
            }).collect(Collectors.toList()));
            return buildingReviews;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }
    }
}