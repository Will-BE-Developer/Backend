package com.sparta.willbe.user.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class PasswordNotFoundException extends BusinessException {
    public PasswordNotFoundException(){
        super(ExceptionCode.PASSWORD_NOT_FOUND);
    }
}
