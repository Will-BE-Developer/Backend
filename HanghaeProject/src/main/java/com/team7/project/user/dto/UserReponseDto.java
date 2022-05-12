package com.team7.project.user.dto;

import com.team7.project.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserReponseDto {
    private UserInfoResponseDto.UserBody user;
}

