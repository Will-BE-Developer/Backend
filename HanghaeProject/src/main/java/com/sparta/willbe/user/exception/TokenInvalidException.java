package com.sparta.willbe.user.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class TokenInvalidException extends BusinessException {
    public TokenInvalidException(){
        super(ExceptionCode.TOKEN_INVALID);
    }
}
