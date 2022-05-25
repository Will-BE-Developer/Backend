package com.sparta.willbe.interview.errors;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private  String message;
    private int status;
    private String code;
//    private List<FieldError> errors;

    @Getter
    @Builder
    public static class FieldError{
        private String field;
        private String value;
        private String reason;
    }

    public ErrorResponse(ErrorCode errorCode){
        this.message = errorCode.getMessage();
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
//        this.errors = new ArrayList<>();
    }
}