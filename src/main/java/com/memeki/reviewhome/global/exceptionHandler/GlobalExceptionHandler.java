package com.memeki.reviewhome.global.exceptionHandler;

import java.io.UnsupportedEncodingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.memeki.reviewhome.global.exception.DefaultException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({ DefaultException.class })
    protected ResponseEntity<ErrorDto> handleCustomException(DefaultException ex) {
        System.out.println("ex default = " + ex);
        ErrorDto errorDto = new ErrorDto(ex.getErrorCode().getStatus(), ex.getErrorCode().getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler({ Exception.class })
    protected ResponseEntity<ErrorDto> handleServerException(Exception ex) throws UnsupportedEncodingException {
        System.out.println("ex = " + ex);
        if(ex instanceof AccessDeniedException) {
            ErrorDto errorDto = new ErrorDto(ErrorCode.UNAUTHORIZED.getStatus(), ErrorCode.UNAUTHORIZED.getMessage());
            return new ResponseEntity<>(errorDto, HttpStatus.valueOf(ErrorCode.UNAUTHORIZED.getStatus()));
        }
        ErrorDto errorDto = new ErrorDto(ErrorCode.INTERNAL_SERVER_ERROR.getStatus(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}