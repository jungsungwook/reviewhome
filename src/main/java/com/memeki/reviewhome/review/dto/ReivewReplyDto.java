package com.memeki.reviewhome.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ReivewReplyDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Request {
        private String uuid;
        private Long userId;
        private int reviewId;
        private String content;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response {
        private int statusCode;
    }
}
