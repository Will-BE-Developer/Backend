package com.sparta.willbe.user.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class ImageSaveFailException extends BusinessException {
    public ImageSaveFailException() {
        super(ExceptionCode.UNABLE_SAVE_PROFILE_IMAGE);
    }
}