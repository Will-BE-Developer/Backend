package com.sparta.willbe._global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", " Invalid Input Value"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", " Invalid Input Value"),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "Access is Denied"),

    // Member examples
//    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST, "M001", "Email is Duplication"),
//    LOGIN_INPUT_INVALID(HttpStatus.BAD_REQUEST, "M002", "Login input is invalid"),


    // Interview
    INTERVIEW_FORBIDDEN_GET(HttpStatus.FORBIDDEN,"I301","현재 사용자는 해당 인터뷰를 조회 할 수 없습니다."),
    INTERVIEW_FORBIDDEN_POST(HttpStatus.FORBIDDEN,"I302","현재 사용자는 해당 인터뷰를 게시 할 수 없습니다."),
    INTERVIEW_FORBIDDEN_UPDATE(HttpStatus.FORBIDDEN,"I303","현재 사용자는 해당 인터뷰를 수정 할 수 없습니다."),
    INTERVIEW_FORBIDDEN_DELETE(HttpStatus.FORBIDDEN,"I304","현재 사용자는 해당 인터뷰를 삭제 할 수 없습니다."),

    INTERVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "I401", "해당 인터뷰가 존재하지 않습니다."),
    DRAFT_NOT_FOUND(HttpStatus.NOT_FOUND,"I402","해당 인터뷰의 초안이 존재하지 않습니다."),

    // must be fixed
    USER_PAYLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "U1301", "프로필 이미지 파일은 5MB 이하만 가능합니다."),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    ExceptionCode(final HttpStatus status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }
}
