package com.sparta.willbe.user.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class ImageFileSizeZeroException extends BusinessException {
    public ImageFileSizeZeroException() {
        super(ExceptionCode.INVALID_IMAGE_SIZE_ZERO);
    }
}