package com.team7.project.question.controller;

import com.team7.project.category.model.CategoryEnum;
import com.team7.project.question.dto.QuestionResponseDto;
import com.team7.project.question.model.Question;
import com.team7.project.question.service.QuestionRandomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class QuestionRandomController {
    private final QuestionRandomService questionRandomService;

//    need refactoring
    @GetMapping("/api/questions/{categoryName}")
    public ResponseEntity<QuestionResponseDto> getRandomQuestionFromCategory(@PathVariable String categoryName) {
        log.info("READ RANDOM QUESTION OF "+ categoryName);

        Question question = questionRandomService.getRandomQuestion(CategoryEnum.valueOf(categoryName));

        QuestionResponseDto body = new QuestionResponseDto(new QuestionResponseDto.data(
                question.getId(),
                question.getCategory().name(),
                question.getContents(),
                question.getReference()
                
        ));

        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
