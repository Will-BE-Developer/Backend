package com.sparta.willbe.user.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class UserDeletedException extends BusinessException {
    public UserDeletedException(){
        super(ExceptionCode.USER_DELETED);
    }
}
