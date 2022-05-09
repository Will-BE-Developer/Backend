package com.team7.project.question.dto;

import com.team7.project.category.model.CategoryEnum;
import com.team7.project.question.model.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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