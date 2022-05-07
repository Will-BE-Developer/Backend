package com.team7.project.interview.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InterviewUpdateRequestDto {
    private String note;
    private Boolean isPublic;
}
