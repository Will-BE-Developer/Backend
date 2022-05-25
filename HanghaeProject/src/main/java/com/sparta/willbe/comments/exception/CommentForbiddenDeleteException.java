package com.sparta.willbe.comments.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class CommentForbiddenDeleteException extends BusinessException {
    public CommentForbiddenDeleteException() {
        super(ExceptionCode.COMMENT_FORBIDDEN_DELETE);
    }
}