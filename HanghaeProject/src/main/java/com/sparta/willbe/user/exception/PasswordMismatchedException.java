package com.sparta.willbe.user.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class PasswordMismatchedException extends BusinessException {
    public PasswordMismatchedException(){
        super(ExceptionCode.PASSWORD_MISMATCHED);
    }
}
