package com.team7.project.question.controller;

import com.team7.project.category.model.CategoryEnum;
import com.team7.project.question.dto.QuestionResponseDto;
import com.team7.project.question.model.Question;
import com.team7.project.question.service.QuestionRandomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class QuestionRandomController {
    private final QuestionRandomService questionRandomService;

    @GetMapping("/api/questions/{categoryName}")
    public QuestionResponseDto getRandomQuestionFromCategory(@PathVariable String categoryName) {
        Question question = questionRandomService.getRandomQuestion(CategoryEnum.valueOf(categoryName));

        return new QuestionResponseDto(question.getContents(),
                question.getReference(),
                question.getCategory().getCategoryName().name());
    }
}