package com.memeki.reviewhome.global.exceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(500, "서버에러. 관리자 문의 바람."),
    INVALID_PARAMETER(400, "유효하지 않은 파라미터입니다."),
    MULTIPLE_RESULT(400, "검색 결과가 여러개입니다. 상세 주소를 입력해주세요."),
    UNAUTHORIZED(401, "인증되지 않은 사용자입니다. 로그인 후 이용해주세요."),
    FORBIDDEN(403, "접근 권한이 없습니다. 관리자에게 문의하세요."),
    NOT_FOUND(404, "검색 결과가 없습니다. 다시 시도해주세요."),;

    private final int status;
    private final String message;
}
