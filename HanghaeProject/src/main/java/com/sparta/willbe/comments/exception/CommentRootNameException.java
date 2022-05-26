package com.sparta.willbe.comments.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class CommentRootNameException extends BusinessException {
    public CommentRootNameException() {
        super(ExceptionCode.INVALID_ROOT_NAME);
    }
}