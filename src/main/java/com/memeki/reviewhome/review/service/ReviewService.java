package com.memeki.reviewhome.review.service;

import com.memeki.reviewhome.review.dto.ReviewCreateDto;
import com.memeki.reviewhome.review.dto.ReviewResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.memeki.reviewhome.global.exception.DefaultException;
import com.memeki.reviewhome.global.exceptionHandler.ErrorCode;
import com.memeki.reviewhome.review.entity.Review;
import com.memeki.reviewhome.review.repository.ReviewRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private ReviewRepository reviewRepository;

    @Autowired
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

//    public String createReview(String targetId) throws Exception {
//        try {
//            Review review = new Review();
//            review.setTargetId(targetId);
//            reviewRepository.save(review);
//            return "";
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new Exception("Review not created");
//        }
//    }

    public ReviewResponseDto createReview(ReviewCreateDto reviewCreateDto) {
        try {
            Review review = new Review();
            review.setType(reviewCreateDto.getType());
            review.setTargetId(reviewCreateDto.getTargetId());
            review.setCreatedBy(reviewCreateDto.getCreatedBy());
            review.setContent(reviewCreateDto.getContent());
            Review savedReview = reviewRepository.save(review);

            ReviewResponseDto reviewResponseDto = new ReviewResponseDto();
            reviewResponseDto.setStatusCode(200);
            reviewResponseDto.setReview(savedReview);
            return reviewResponseDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("리뷰가 등록되지않았습니다.");
        }

    }

    public List<ReviewResponseDto> findReviewByType(String type) {
        List<Review> reviews = reviewRepository.findReviewByType(type);
        return reviews.stream()
                .map(review -> {
                    ReviewResponseDto dto = new ReviewResponseDto();
                    dto.setReview(review);
                    dto.setStatusCode(200);
                    return dto;
                })
                .collect(Collectors.toList());

    }

    public ReviewResponseDto findReviewByTypeAndTargetId(String type, String targetId) {
        Review review = reviewRepository.findReviewByTypeAndTargetId(type, targetId);
        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setReview(review);
        dto.setStatusCode(200);
        return dto;
    }

    public void updateReviewContentByTypeAndUser(String type, String newContent, String targetId) {
        List<Review> reviews = reviewRepository.findReviewByType(type);
        for (Review review : reviews) {
            if (review.getTargetId().equals(targetId)) {
                review.setContent(newContent);
                reviewRepository.save(review);
            }
        }


    }
}





