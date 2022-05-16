package com.team7.project.likes.dto;


import com.team7.project.interview.dto.InterviewInfoResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeRequestDto {
    private Long  interviewId;
    private String time;
}
