package com.memeki.reviewhome.global.exception;

import com.memeki.reviewhome.global.exceptionHandler.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DefaultException extends RuntimeException {
    private final ErrorCode errorCode;
}