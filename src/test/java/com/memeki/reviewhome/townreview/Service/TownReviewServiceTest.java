package com.memeki.reviewhome.townreview.Service;

import com.memeki.reviewhome.townreview.dto.TownReviewCreateDto;
import com.memeki.reviewhome.townreview.entity.TownReview;
import com.memeki.reviewhome.townreview.entity.TownReviewContent;
import com.memeki.reviewhome.townreview.repository.TownReviewContentRepository;
import com.memeki.reviewhome.townreview.repository.TownReviewRepository;
import com.memeki.reviewhome.townreview.service.TownReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TownReviewServiceTest {

    @InjectMocks
    private TownReviewService townReviewService;

    @Mock
    private TownReviewRepository townReviewRepository;

    @Mock
    private TownReviewContentRepository townReviewContentRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testTownCreateReview() {
        TownReviewCreateDto townReviewCreateDto = new TownReviewCreateDto();
        townReviewCreateDto.setType("town");
        townReviewCreateDto.setTargetId("targetId");
        townReviewCreateDto.setUserId(1L);
        townReviewCreateDto.setContent("content");
        townReviewCreateDto.setTitle("title");

        TownReview townReview = new TownReview();
        TownReviewContent townReviewContent = new TownReviewContent();

        when(townReviewRepository.save(any(TownReview.class))).thenReturn(townReview);
        when(townReviewContentRepository.save(any(TownReviewContent.class))).thenReturn(townReviewContent);

        townReviewService.towncreateReview(townReviewCreateDto);
    }
}