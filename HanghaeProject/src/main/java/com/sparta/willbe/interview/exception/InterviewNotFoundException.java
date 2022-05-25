package com.sparta.willbe.interview.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class InterviewNotFoundException extends BusinessException {
    public InterviewNotFoundException() {
        super(ExceptionCode.INTERVIEW_NOT_FOUND);
    }
}
