package com.sparta.willbe.scrap.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class ScrapDeleteConflictException extends BusinessException {
    public ScrapDeleteConflictException() {
        super(ExceptionCode.SCRAP_DELETE_CONFLICT);
    }
}
