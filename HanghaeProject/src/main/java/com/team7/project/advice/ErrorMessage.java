package com.team7.project.advice;

import org.springframework.http.HttpStatus;

public enum ErrorMessage {

    INVALID_INTERVIEW_VIEW(HttpStatus.BAD_REQUEST,"현재 사용자는 해당 인터뷰를 조회 할 수 없습니다."),
    INVALID_INTERVIEW_POST(HttpStatus.BAD_REQUEST,"현재 사용자는 해당 인터뷰를 게시 할 수 없습니다."),
    INVALID_INTERVIEW_UPDATE(HttpStatus.BAD_REQUEST,"현재 사용자는 해당 인터뷰를 수정 할 수 없습니다."),
    INVALID_INTERVIEW_DELETE(HttpStatus.BAD_REQUEST,"현재 사용자는 해당 인터뷰를 삭제 할 수 없습니다."),


    INVALID_PAGINATION_SIZE(HttpStatus.BAD_REQUEST,"한 페이지 단위(per)는 0보다 커야 합니다."),
    INVALID_PAGINATION_CATEGORY(HttpStatus.BAD_REQUEST,"잘못된 카테고리를 입력했습니다."),

    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED,"해당 요청은 로그인이 필요합니다."),

    NOT_FOUND_USER(HttpStatus.NOT_FOUND,"해당 유저가 존재하지 않습니다."),
    NOT_FOUND_LOGIN_USER(HttpStatus.NOT_FOUND,"로그인 정보에 해당하는 유저를 찾을 수 없습니다."),
    NOT_FOUND_INTERVIEW(HttpStatus.NOT_FOUND,"해당 인터뷰가 존재하지 않습니다."),
    NOT_FOUND_DRAFT(HttpStatus.NOT_FOUND,"해당 인터뷰의 초안이 존재하지 않습니다."),
    NOT_FOUND_QUESTION(HttpStatus.NOT_FOUND,"해당 면접 질문이 존재하지 않습니다."),

    CONFLICT_SCRAP_POST(HttpStatus.CONFLICT,"이미 스크랩한 게시글 입니다."),
    CONFLICT_SCRAP_DELETE(HttpStatus.CONFLICT,"해당 스크랩 정보가 존재하지 않습니다."),

    INVALID_USER_REQUEST(HttpStatus.BAD_REQUEST, "로그아웃 후에 회원가입을 진행 해 주세요."),
    PASSWORD_MISSMATCHED(HttpStatus.BAD_REQUEST, "비밀번호와 비밀번호확인이 일치하지 않습니다.");


    ;

    private final RestException exception;

    ErrorMessage(HttpStatus httpStatus, String message){
        this.exception = new RestException(httpStatus, message);
    }

    public RestException throwError(){
        return this.exception;
    }

}
