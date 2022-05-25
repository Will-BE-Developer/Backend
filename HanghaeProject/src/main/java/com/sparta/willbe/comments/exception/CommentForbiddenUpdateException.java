package com.sparta.willbe.comments.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class CommentForbiddenUpdateException extends BusinessException {
    public CommentForbiddenUpdateException() {
        super(ExceptionCode.COMMENT_FORBIDDEN_UPDATE);
    }
}