package com.team7.project.question.dto;

import com.team7.project.question.model.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class QuestionResponseDto {
    private String id;
    private String contents;

    private String reference;



}