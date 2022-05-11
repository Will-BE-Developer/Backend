package com.team7.project.user.dto;

import com.team7.project.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserReponseDto {
    private Long id;
    private String nickname;
    private String githubLink;
    private String introduce;
    private String profileImageUrl;

    public UserReponseDto(User user){
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.githubLink = user.getGithubLink();
        this.introduce = user.getIntroduce();
        this.profileImageUrl = user.getProfileImageUrl();
    }
}

