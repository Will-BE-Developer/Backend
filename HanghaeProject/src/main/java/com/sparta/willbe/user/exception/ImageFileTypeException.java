package com.sparta.willbe.user.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class ImageFileTypeException extends BusinessException {
    public ImageFileTypeException() {
        super(ExceptionCode.INVALID_IMAGE_FILE);
    }
}