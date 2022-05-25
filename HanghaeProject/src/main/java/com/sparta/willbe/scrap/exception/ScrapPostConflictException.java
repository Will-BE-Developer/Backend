package com.sparta.willbe.scrap.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class ScrapPostConflictException extends BusinessException {
    public ScrapPostConflictException() {
        super(ExceptionCode.SCRAP_POST_CONFLICT);
    }
}
