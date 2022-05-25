package com.sparta.willbe.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(RestException.class)
    public ResponseEntity<Map<String, Object>> handler(RestException e) {
        Map<String, Object> resBody = new HashMap<>();
        resBody.put("message", e.getMessage());

        return new ResponseEntity<>(resBody, e.getHttpStatus());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
    public ResponseEntity handleMultipartException(MaxUploadSizeExceededException e) {

        Map<String, String> result = new HashMap<>();
        result.put("message", "프로필 이미지 파일은 5MB 이하만 가능합니다.");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(result);
    }
}