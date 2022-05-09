package com.team7.project.interview.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InterviewPostRequestDto {

    private String note;

    private Long questionId;

    private Boolean isPublic;

}
