package com.team7.project.question.dto;

import com.team7.project.category.model.CategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionRequestDto {

    private String contents;

    private String reference;

    private String category;

}
