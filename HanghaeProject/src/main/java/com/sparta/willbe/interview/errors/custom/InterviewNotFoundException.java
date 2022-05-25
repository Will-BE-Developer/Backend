package com.sparta.willbe.interview.errors.custom;

import com.sparta.willbe.interview.errors.ErrorCode;

public class InterviewNotFoundException extends BusinessException{
    public InterviewNotFoundException() {
        super(ErrorCode.INTERVIEW_NOT_FOUND);
    }
}
