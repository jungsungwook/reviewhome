package com.memeki.reviewhome.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ReviewLikeDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Request {
        private int reviewId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response {
        private int statusCode;
        private int type;
    }
}
