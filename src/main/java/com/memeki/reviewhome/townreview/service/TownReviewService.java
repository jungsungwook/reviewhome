package com.memeki.reviewhome.townreview.service;

import com.memeki.reviewhome.townreview.dto.TownReviewCreateDto;
import com.memeki.reviewhome.townreview.dto.TownReviewResponseDto;
import com.memeki.reviewhome.townreview.entity.TownReviewContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.memeki.reviewhome.global.exception.DefaultException;
import com.memeki.reviewhome.global.exceptionHandler.ErrorCode;
import com.memeki.reviewhome.townreview.entity.TownReview;
import com.memeki.reviewhome.townreview.repository.TownReviewRepository;
import com.memeki.reviewhome.townreview.repository.TownReviewContentRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TownReviewService {
    private TownReviewContentRepository townReviewContentRepository;
    private TownReviewRepository townReviewRepository;

    @Autowired
    public TownReviewService(TownReviewRepository townReviewRepository, TownReviewContentRepository townReviewContentRepository) {
        this.townReviewRepository = townReviewRepository;
        this.townReviewContentRepository = townReviewContentRepository;
    }

    public TownReview getReviewByReviewId(int reviewId) throws Exception {
        try {
            return townReviewRepository.findReviewById(reviewId);
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
    //towncreateReview를 테스트하고싶어
    public String towncreateReview(TownReviewCreateDto townReviewCreateDto) {
        if(townReviewCreateDto.getContent()==null){
            throw new IllegalArgumentException("공백을 입력하시면 안됩니다.");
        }
        try {
            TownReview townreview = new TownReview();
            townreview.setType("town");
            townreview.setTargetId(townReviewCreateDto.getTargetId());
            townreview.setCreatedBy(townReviewCreateDto.getUserId());
            townreview.setContent(townReviewCreateDto.getContent());
            townReviewRepository.save(townreview);
            //TownReview savedTownReview = townReviewRepository.save(townreview);

            TownReviewContent townReviewContent = new TownReviewContent();
            townReviewContent.setReviewId(townreview.getId());
            townReviewContent.setTitle(townReviewCreateDto.getTitle());
            townReviewContent.setContent(townReviewCreateDto.getContent());
            townReviewContent.setReviewType(townReviewCreateDto.getType());
            townReviewContentRepository.save(townReviewContent);
            //TownReviewContent savedTownReviewContent = townReviewContentRepository.save(townReviewContent);

            return "리뷰 생성에 성공하였습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("리뷰등록에 실패하였습니다.");
        }

    }

    public List<TownReview> findAllTownReviewsByTypeAndCreatedBy(String type, Long createdBy) {
        if(!"town".equals(type)){
            throw new IllegalArgumentException(" 동네리뷰가 아닙니다.");
        }
        if(createdBy==null){
            throw new IllegalArgumentException("userId가 올바르지않습니다.");
        }
        try{
            return townReviewRepository.findTownReviewByTypeAndCreatedBy(type, createdBy);
        } catch (Exception e) {
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }
    }

//    public List<TownReviewResponseDto> findReviewByType(String type) {
//        List<TownReview> reviews = townReviewRepository.findReviewByType(type);
//        return reviews.stream()
//                .map(townreview -> {
//                    TownReviewResponseDto dto = new TownReviewResponseDto();
//                    dto.setTownReview(townreview);
//                    dto.setStatusCode(200);
//                    return dto;
//                })
//                .collect(Collectors.toList());
//
//    }


    public TownReviewResponseDto findReviewByTypeAndTargetId(String type, String targetId) {
        TownReview townReview = townReviewRepository.findReviewByTypeAndTargetId(type, targetId);
        TownReviewResponseDto dto = new TownReviewResponseDto();
        dto.setTownReview(townReview);
        dto.setStatusCode(200);
        return dto;
    }

    public void updateReviewContentByTypeAndUser(String type, String newContent, String targetId) {
        List<TownReview> reviews = townReviewRepository.findReviewByType(type);
        for (TownReview review : reviews) {
            if (review.getTargetId().equals(targetId)) {
                review.setContent(newContent);
                townReviewRepository.save(review);
            }
        }


    }

}
