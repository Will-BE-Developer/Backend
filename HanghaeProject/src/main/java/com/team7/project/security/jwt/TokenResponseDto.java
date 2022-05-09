package com.team7.project.security.jwt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponseDto {
    private String Authorization;
}
