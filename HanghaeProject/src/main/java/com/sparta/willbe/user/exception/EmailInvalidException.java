package com.sparta.willbe.user.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class EmailInvalidException extends BusinessException {
    public EmailInvalidException(){
        super(ExceptionCode.EMAIL_INVALID);
    }
}
