package com.sparta.willbe.interview.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewPostRequestDto {

    private String note;
    private Long questionId;
    private Boolean isPublic;

}
