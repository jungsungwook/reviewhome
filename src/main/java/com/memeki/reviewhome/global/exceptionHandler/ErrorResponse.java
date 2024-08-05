package com.memeki.reviewhome.global.exceptionHandler;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ErrorResponse {
    private int statusCode;
    private String message;
}