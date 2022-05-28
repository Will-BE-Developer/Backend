package com.sparta.willbe._global.pagination.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class PaginationPerInvalidException extends BusinessException {
    public PaginationPerInvalidException() {
        super(ExceptionCode.PAGINATION_PER_INVALID);
    }
}
