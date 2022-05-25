package com.sparta.willbe.interview.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class InterviewForbiddenDeleteException extends BusinessException {
    public InterviewForbiddenDeleteException() {
        super(ExceptionCode.INTERVIEW_FORBIDDEN_DELETE);
    }
}
