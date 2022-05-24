package com.sparta.willbe.user.errors;

public class UserAlreadyExistException extends RuntimeException{
    private static final String MESSAGE = "이미 로그인한 사용자 입니다.";
    public UserAlreadyExistException () {
        super(MESSAGE);
    }
}
