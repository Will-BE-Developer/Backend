package com.sparta.willbe.interview.errors;

import com.sparta.willbe.interview.errors.custom.BusinessException;
import com.sparta.willbe.interview.errors.custom.InterviewNotFoundException;
import com.sparta.willbe.user.errors.UserAlreadyExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
//naming must be refactored
public class InterviewExceptionhandler extends ResponseEntityExceptionHandler {

//    Worse
//    @ExceptionHandler(InterviewNotFoundException.class)
//    public ResponseEntity<ErrorResponse> handleUserAlreadyExist (UserAlreadyExistException e) {
//        log.error("InterviewNotFoundException", e);
//        final ErrorResponse response = new ErrorResponse(ErrorCode.INTERVIEW_NOT_FOUND);
//        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//    }

//    Better
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException e) {
//        log.error("BusinessException {}", e.getErrorCode().getMessage());
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse response = new ErrorResponse(errorCode);
        return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
    }

//naming must be refactored
//    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
//    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
//        log.error("handleHttpRequestMethodNotSupportedException", e);
//        final ErrorResponse response = new ErrorResponse(ErrorCode.METHOD_NOT_ALLOWED);
//        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
//    }
//


}
