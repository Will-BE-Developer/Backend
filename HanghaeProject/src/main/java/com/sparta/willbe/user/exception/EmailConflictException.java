package com.sparta.willbe.user.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class EmailConflictException extends BusinessException {
    public EmailConflictException(){
        super(ExceptionCode.EMAIL_CONFLICT);
    }
}
