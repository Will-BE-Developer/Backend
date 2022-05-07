package com.team7.project.security.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
public class Token {
    private String token;
//    private String refreshToken;

    public Token(String token) {
        this.token = token;
//        this.refreshToken = refreshToken;
    }
}
