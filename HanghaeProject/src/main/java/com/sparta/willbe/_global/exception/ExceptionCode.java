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
    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST, "M001", "Email is Duplication"),
    LOGIN_INPUT_INVALID(HttpStatus.BAD_REQUEST, "M002", "Login input is invalid"),

    //USER
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"U001","해당 유저가 존재하지 않습니다."),
    USER_AlREADY_FOUND(HttpStatus.BAD_REQUEST,"U002", "이미 로그인된 사용자 입니다."),
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"U003","해당 요청은 로그인이 필요합니다."),

    USER_DELETED(HttpStatus.BAD_REQUEST,"U004","탈퇴 처리된 회원입니다."),

    //Login & Register
    PASSWORD_NOT_FOUND(HttpStatus.BAD_REQUEST,"R001","비밀번호가 일치하지 않습니다."),
    PASSWORD_MISMATCHED(HttpStatus.BAD_REQUEST, "R002","비밀번호와 비밀번호확인이 일치하지 않습니다."),

    EMAIL_INVALID(HttpStatus.UNAUTHORIZED,"R003","이메일 인증이 되지 않았습니다."),
    EMAIL_CONFLICT(HttpStatus.CONFLICT, "R004","이미 존재하는 이메일 입니다."),

    TOKEN_INVALID(HttpStatus.UNAUTHORIZED,"R005","유효한 토큰이 아닙니다."),

    // Interview
    INTERVIEW_FORBIDDEN_GET(HttpStatus.FORBIDDEN,"I301","현재 사용자는 해당 인터뷰를 조회 할 수 없습니다."),
    INTERVIEW_FORBIDDEN_POST(HttpStatus.FORBIDDEN,"I302","현재 사용자는 해당 인터뷰를 게시 할 수 없습니다."),
    INTERVIEW_FORBIDDEN_UPDATE(HttpStatus.FORBIDDEN,"I303","현재 사용자는 해당 인터뷰를 수정 할 수 없습니다."),
    INTERVIEW_FORBIDDEN_DELETE(HttpStatus.FORBIDDEN,"I304","현재 사용자는 해당 인터뷰를 삭제 할 수 없습니다."),

    INTERVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "I401", "해당 인터뷰가 존재하지 않습니다."),
    DRAFT_NOT_FOUND(HttpStatus.NOT_FOUND,"I402","해당 인터뷰의 초안이 존재하지 않습니다."),

    // Question
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Q401", "해당 면접 질문이 존재하지 않습니다."),

    //Pagination
    PAGINATION_CATEGORY_INVALID(HttpStatus.BAD_REQUEST,"P001","잘못된 카테고리를 입력했습니다."),
    PAGINATION_PER_INVALID(HttpStatus.BAD_REQUEST,"P002","한 페이지 단위(per)는 0보다 커야 합니다."),

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
