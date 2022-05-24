package com.sparta.willbe.user.errors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.FieldError;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private  String message;
    private int status;
    private List<FieldError> errors;
    private String code;

    @Getter
    @NoArgsConstructor
    public static class FieldError{
        private String field;
        private String value;
        private String reason;
    }
}
