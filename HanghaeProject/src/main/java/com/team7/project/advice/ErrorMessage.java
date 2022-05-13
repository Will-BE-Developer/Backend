package com.team7.project.advice;

import org.springframework.http.HttpStatus;

public enum ErrorMessage {
    NOT_FOUND_USER(HttpStatus.NOT_FOUND,"해당 유저를 찾을 수 없습니다."),
    NOT_FOUND_INTERVIEW(HttpStatus.NOT_FOUND,"해당 인터뷰가 존재하지 않습니다.");

    private final RestException exception;

    ErrorMessage(HttpStatus httpStatus, String message){
        this.exception = new RestException(httpStatus, message);
    }

    public RestException throwError(){
        return this.exception;
    }

}
