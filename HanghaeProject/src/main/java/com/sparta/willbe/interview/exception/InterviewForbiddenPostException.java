package com.sparta.willbe.interview.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class InterviewForbiddenPostException extends BusinessException {
    public InterviewForbiddenPostException() {
        super(ExceptionCode.INTERVIEW_FORBIDDEN_POST);
    }
}
