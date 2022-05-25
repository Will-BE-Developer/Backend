package com.sparta.willbe.user.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(){
        super(ExceptionCode.USER_NOT_FOUND);
    }
}
