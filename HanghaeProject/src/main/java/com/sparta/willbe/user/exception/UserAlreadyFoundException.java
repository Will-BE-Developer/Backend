package com.sparta.willbe.user.exception;

import com.sparta.willbe._global.exception.ExceptionCode;
import com.sparta.willbe._global.exception.BusinessException;

public class UserAlreadyFoundException extends BusinessException {
    public UserAlreadyFoundException() {
        super(ExceptionCode.USER_AlREADY_FOUND);
    }
}
