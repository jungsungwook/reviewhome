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
    NOT_FOUND(404, "검색 결과가 없습니다. 다시 시도해주세요."),
    ALREADY_EXIST(409, "이미 존재하는 데이터입니다."),
    ALREADY_ENTERED(409, "이미 가입한 커뮤니티입니다."),
    ALREADY_NICKNAME(409, "이미 사용중인 닉네임입니다."),
    NEED_PASSWORD(401, "비밀번호가 필요합니다."),
    INVALID_PASSWORD(401, "비밀번호가 일치하지 않습니다."),
    INVALID_FILE(400, "유효하지 않은 파일입니다."),
    FILE_PROCESSING_ERROR(500, "파일 처리 중 오류가 발생했습니다."),
    NEED_ENTER(403, "커뮤니티에 가입해야 이용할 수 있습니다.");
    private final int status;
    private final String message;
}
