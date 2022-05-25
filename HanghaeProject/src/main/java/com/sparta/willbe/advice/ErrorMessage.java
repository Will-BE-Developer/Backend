package com.sparta.willbe.advice;

import org.springframework.http.HttpStatus;

public enum ErrorMessage{

//    MOVED
    INVALID_INTERVIEW_VIEW(HttpStatus.BAD_REQUEST,"현재 사용자는 해당 인터뷰를 조회 할 수 없습니다."),
    //    MOVED
    INVALID_INTERVIEW_POST(HttpStatus.BAD_REQUEST,"현재 사용자는 해당 인터뷰를 게시 할 수 없습니다."),
    //    MOVED
    INVALID_INTERVIEW_UPDATE(HttpStatus.BAD_REQUEST,"현재 사용자는 해당 인터뷰를 수정 할 수 없습니다."),
    //    MOVED
    INVALID_INTERVIEW_DELETE(HttpStatus.BAD_REQUEST,"현재 사용자는 해당 인터뷰를 삭제 할 수 없습니다."),

    //MOVED
    INVALID_PAGINATION_SIZE(HttpStatus.BAD_REQUEST,"한 페이지 단위(per)는 0보다 커야 합니다."),
    //MOVED
    INVALID_PAGINATION_CATEGORY(HttpStatus.BAD_REQUEST,"잘못된 카테고리를 입력했습니다."),

    //MOVED
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,"유효한 토큰이 아닙니다."),
    //MOVED
    INVALID_EMAIL(HttpStatus.BAD_REQUEST,"이메일 인증이 되지 않았습니다."),
    //MOVED
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED,"해당 요청은 로그인이 필요합니다."),

    //MOVED
    DELETED_USER(HttpStatus.BAD_REQUEST,"탈퇴 처리된 회원입니다."),
    //MOVED
    NOT_FOUND_USER(HttpStatus.NOT_FOUND,"해당 유저가 존재하지 않습니다."),
    //MOVED
    NOT_FOUND_LOGIN_USER(HttpStatus.NOT_FOUND,"로그인 정보에 해당하는 유저를 찾을 수 없습니다."),

    //    MOVED
    NOT_FOUND_INTERVIEW(HttpStatus.NOT_FOUND,"해당 인터뷰가 존재하지 않습니다."),
    //    MOVED
    NOT_FOUND_DRAFT(HttpStatus.NOT_FOUND,"해당 인터뷰의 초안이 존재하지 않습니다."),

    //MOVED
    NOT_FOUND_QUESTION(HttpStatus.NOT_FOUND,"해당 면접 질문이 존재하지 않습니다."),
    //MOVED
    NOT_FOUND_PASSWORD(HttpStatus.NOT_FOUND,"비밀번호가 일치하지 않습니다."),
    //MOVED
    USER_AlREADY_FOUND(HttpStatus.BAD_REQUEST, "이미 로그인된 사용자 입니다."),

    //MOVED
    CONFLICT_SCRAP_POST(HttpStatus.CONFLICT,"이미 스크랩한 게시글 입니다."),
    //MOVED
    CONFLICT_SCRAP_DELETE(HttpStatus.CONFLICT,"해당 스크랩 정보가 존재하지 않습니다."),

    //MOVED
    CONFLICT_USER_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일 입니다."),
    //MOVED
    PASSWORD_MISMATCHED(HttpStatus.BAD_REQUEST, "비밀번호와 비밀번호확인이 일치하지 않습니다."),

    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND,"해당 댓글은 존재하지 않습니다."),
    INVALID_ROOT_ID(HttpStatus.BAD_REQUEST, "수정하려는 댓글의 RootId가 일치하지 않습니다."),
    INVALID_ROOT_NAME(HttpStatus.BAD_REQUEST, "수정하려는 댓글의 RootName이 일치하지 않습니다."),
    INVALID_IMAGE_FILE(HttpStatus.BAD_REQUEST, "프로필 이미지는 png, jpg, gif 확장자만 가능합니다."),
    INVALID_IMAGE_SIZE_ZERO(HttpStatus.BAD_REQUEST, "프로필 이미지 파일이 0바이트 입니다."),
    INVALID_IMAGE_SIZE(HttpStatus.BAD_REQUEST, "프로필 이미지 파일은 5MB 이하만 가능합니다."),
    UNABLE_UPLOAD_TO_S3(HttpStatus.INTERNAL_SERVER_ERROR, "프로필 이미지 업로드가 실패하였습니다."),
    UNABLE_SAVE_PROFILE_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR, "프로필 이미지 저장 또는 업로드가 실패하였습니다."),
    FAIL_DELETE_INTERVIEW(HttpStatus.INTERNAL_SERVER_ERROR, "면접왕인 인터뷰 삭제가 실패하였습니다."),
    UNABLE_DELETE_INTERVIEW_ON_WEEKLY(HttpStatus.BAD_REQUEST, "면접왕으로 선정된 인터뷰는 삭제가 불가합니다."),
    NOT_FOUND_INTERVIEW_IN_WEEKLY(HttpStatus.GONE,"위클리 테이블에서 해당 인터뷰가 존재하지 않습니다."),
    ;

    private final RestException exception;

    ErrorMessage(HttpStatus httpStatus, String message){
        this.exception = new RestException(httpStatus, message);
    }

    public RestException throwError(){
        return this.exception;
    }

}
