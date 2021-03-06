package com.sparta.willbe.interview.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewUpdateRequestDto {
    private String note;
    private Boolean isPublic;
}
