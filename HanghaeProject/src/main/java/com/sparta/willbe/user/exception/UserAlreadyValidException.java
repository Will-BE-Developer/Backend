package com.sparta.willbe.user.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class UserAlreadyValidException extends BusinessException {
    public UserAlreadyValidException(){
        super(ExceptionCode.USER_ALREADY_VALID);
    }
}
