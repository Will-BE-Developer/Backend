package com.sparta.willbe.user.exception;

import com.sparta.willbe._global.exception.ErrorResponse;
import com.sparta.willbe._global.exception.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class ImageFileSizeOverException extends MaxUploadSizeExceededException{

    @Value("${spring.servlet.multipart.maxFileSize}")
    public String maxFileSize;

    public String getMaxFileSize(){
        return maxFileSize;
    }

    public ImageFileSizeOverException() {
        super(5242880L);
    }

    public ImageFileSizeOverException(long maxUploadSize) {
        super(maxUploadSize);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> fileSizeOver(MaxUploadSizeExceededException e){
        ErrorResponse response = new ErrorResponse(ExceptionCode.INVALID_IMAGE_SIZE);
        log.info("IMAGE FILE SIZE EXCEPTION >> fileSizeOver()");
        return new ResponseEntity<>(response, ExceptionCode.INVALID_IMAGE_SIZE.getStatus());
    }
}