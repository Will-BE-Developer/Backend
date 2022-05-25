package com.sparta.willbe.user.exception;

import com.sparta.willbe._global.exception.ExceptionCode;
import com.sparta.willbe._global.exception.BusinessException;

public class UserAlreadyExistException extends BusinessException {
    private static final String MESSAGE = "이미 로그인한 사용자 입니다.";
    public UserAlreadyExistException () {
//  must be fixed
        super(ExceptionCode.EMAIL_DUPLICATION);
    }
}
