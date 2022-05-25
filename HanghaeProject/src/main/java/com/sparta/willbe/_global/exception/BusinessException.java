package com.sparta.willbe._global.exception;

import com.sparta.willbe._global.exception.ExceptionCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ExceptionCode errorCode;

    public BusinessException(String message, ExceptionCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(ExceptionCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}