package com.sparta.willbe._global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // Common
    INPUT_VALUE_INVALID(HttpStatus.BAD_REQUEST, "C001", "올바르지 않은 값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", " Invalid Input Value"),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "Access is Denied"),
    // Member examples
    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST, "M001", "Email is Duplication"),
    LOGIN_INPUT_INVALID(HttpStatus.BAD_REQUEST, "M002", "Login input is invalid"),

    //USER
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"U001","해당 유저가 존재하지 않습니다."),
    USER_AlREADY_FOUND(HttpStatus.BAD_REQUEST,"U002", "이미 로그인된 사용자 입니다."),
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"U003","해당 요청은 로그인이 필요합니다."),
    USER_ALREADY_VALID(HttpStatus.BAD_REQUEST,"U003","이미 인증 완료된 사용자 입니다."),
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

    // Scrap
    SCRAP_POST_CONFLICT(HttpStatus.CONFLICT,"S901","이미 스크랩한 게시글 입니다."),
    SCRAP_DELETE_CONFLICT(HttpStatus.CONFLICT,"S902","해당 스크랩 정보가 존재하지 않습니다."),

    //Pagination
    PAGINATION_CATEGORY_INVALID(HttpStatus.BAD_REQUEST,"P001","잘못된 카테고리를 입력했습니다."),
    PAGINATION_PER_INVALID(HttpStatus.BAD_REQUEST,"P002","한 페이지 단위(per)는 0보다 커야 합니다."),

    // Comment
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND,"C101", "해당 댓글은 존재하지 않습니다."),
    INVALID_ROOT_ID(HttpStatus.BAD_REQUEST, "C102", "수정하려는 댓글의 RootId가 일치하지 않습니다."),
    INVALID_ROOT_NAME(HttpStatus.BAD_REQUEST, "C103", "수정하려는 댓글의 RootName이 일치하지 않습니다."),
    COMMENT_FORBIDDEN_DELETE(HttpStatus.FORBIDDEN,"C104","현재 사용자는 해당 댓글을 삭제할 수 없습니다."),
    COMMENT_FORBIDDEN_UPDATE(HttpStatus.FORBIDDEN,"C105","현재 사용자는 해당 댓글을 수정할 수 없습니다."),

    // Profile Image File
    INVALID_IMAGE_FILE(HttpStatus.BAD_REQUEST, "F101","프로필 이미지는 png, jpg, gif 확장자만 가능합니다."),
    INVALID_IMAGE_SIZE_ZERO(HttpStatus.BAD_REQUEST,"F102", "프로필 이미지 파일이 0바이트 입니다."),
    INVALID_IMAGE_SIZE(HttpStatus.PAYLOAD_TOO_LARGE, "F103","프로필 이미지 파일은 5MB 이하만 가능합니다."),
    UNABLE_UPLOAD_TO_S3(HttpStatus.INTERNAL_SERVER_ERROR, "F104","프로필 이미지 업로드가 실패하였습니다."),
    UNABLE_SAVE_PROFILE_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR,"F105", "프로필 이미지 저장 또는 업로드가 실패하였습니다."),


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
