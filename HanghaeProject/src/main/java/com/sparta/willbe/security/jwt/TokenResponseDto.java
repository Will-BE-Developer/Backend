package com.sparta.willbe.security.jwt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponseDto {
    private String Authorization;
}
