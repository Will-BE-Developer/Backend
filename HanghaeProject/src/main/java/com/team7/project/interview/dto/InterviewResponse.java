package com.team7.project.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class InterviewResponse {
    private Long id;

    private UesrBody user;

    private String video;
    private String thumbnail;

    private String question;
    private String badge;
    private String note;
    private Boolean scrapsMe;
    private Long scrapsCount;
    private Long likesCount;
    private Boolean isPublic;

    private String createdAt;
    private String updatedAt;

//    need refactoring
    @Getter
    @AllArgsConstructor
    @Builder
    public static class UesrBody{
        private Long id;
        private String nickName;
        private String githubLink;
        private String profileImageUrl;
        private String introduce;
    }


}
