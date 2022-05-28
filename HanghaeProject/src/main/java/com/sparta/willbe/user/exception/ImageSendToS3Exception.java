package com.sparta.willbe.user.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;

public class ImageSendToS3Exception extends BusinessException {
    public ImageSendToS3Exception() {
        super(ExceptionCode.UNABLE_UPLOAD_TO_S3);
    }
}