package com.sparta.willbe.interview.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class InterviewForbiddenGetException extends BusinessException {
    public InterviewForbiddenGetException() {
        super(ExceptionCode.INTERVIEW_FORBIDDEN_GET);
    }
}
