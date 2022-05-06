package com.team7.project.advice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class RestException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private HttpStatus httpStatus;
    private String message;

    public RestException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
