package com.sparta.willbe.interview.errors;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class DraftNotFoundException extends BusinessException {
    public DraftNotFoundException() {
        super(ExceptionCode.DRAFT_NOT_FOUND);
    }
}
