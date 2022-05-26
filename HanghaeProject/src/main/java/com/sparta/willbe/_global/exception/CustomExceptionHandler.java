package com.sparta.willbe._global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException e) {
        final ExceptionCode errorCode = e.getErrorCode();
        final ErrorResponse response = new ErrorResponse(errorCode);
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

//    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
//    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
//        log.error("handleHttpRequestMethodNotSupportedException: {}", e.getMessage());
//        final ErrorResponse response = new ErrorResponse(ExceptionCode.METHOD_NOT_ALLOWED);
//        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
//    }



}
