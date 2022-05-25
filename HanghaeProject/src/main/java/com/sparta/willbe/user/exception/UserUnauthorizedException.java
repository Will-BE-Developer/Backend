package com.sparta.willbe.user.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class UserUnauthorizedException extends BusinessException {
    public UserUnauthorizedException(){
        super(ExceptionCode.USER_UNAUTHORIZED);
    }
}
