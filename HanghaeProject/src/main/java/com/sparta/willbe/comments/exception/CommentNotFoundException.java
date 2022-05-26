package com.sparta.willbe.comments.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class CommentNotFoundException extends BusinessException {
    public CommentNotFoundException() {
        super(ExceptionCode.NOT_FOUND_COMMENT);
    }
}
