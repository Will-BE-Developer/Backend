package com.sparta.willbe.interview.errors.exceptions;

public class InterviewNotFoundException extends RuntimeException{
    private static final String MESSAGE = "해당 인터뷰가 존재하지 않습니다.";
    public InterviewNotFoundException() {
        super(MESSAGE);
    }
}
