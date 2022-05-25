package com.sparta.willbe._global.pagination.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class PaginationCategoryInvalidException extends BusinessException {
    public PaginationCategoryInvalidException() {
        super(ExceptionCode.QUESTION_NOT_FOUND);
    }
}
