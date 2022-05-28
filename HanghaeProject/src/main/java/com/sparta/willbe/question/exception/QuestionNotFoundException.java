package com.sparta.willbe.question.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class QuestionNotFoundException extends BusinessException {
    public QuestionNotFoundException() {
        super(ExceptionCode.QUESTION_NOT_FOUND);
    }
}
