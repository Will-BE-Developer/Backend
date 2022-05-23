package com.sparta.willbe.likes.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeRequestDto {
    private Long  interviewId;
    private int time;
    private int count;
}
