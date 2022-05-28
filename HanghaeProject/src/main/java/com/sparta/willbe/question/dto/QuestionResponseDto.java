package com.sparta.willbe.question.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class QuestionResponseDto {
    private QuestionResponseDto.data question;

    @Getter
    @AllArgsConstructor
    public static class data {
        private Long id;
        private String category;
        private String contents;
        private String reference;
    }
}