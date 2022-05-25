package com.sparta.willbe.interview.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class InterviewForbiddenUpdateException extends BusinessException {
    public InterviewForbiddenUpdateException() {
        super(ExceptionCode.INTERVIEW_FORBIDDEN_UPDATE);
    }
}
