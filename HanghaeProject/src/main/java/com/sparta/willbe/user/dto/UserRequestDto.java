package com.sparta.willbe.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    private String nickname;
    private String githubLink;
    private MultipartFile profileImage;
    private String introduce;
}


