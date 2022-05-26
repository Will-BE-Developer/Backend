package com.sparta.willbe.question.controller;

import com.sparta.willbe._global.pagination.exception.PaginationCategoryInvalidException;
import com.sparta.willbe.advice.ErrorMessage;
import com.sparta.willbe.category.model.CategoryEnum;
import com.sparta.willbe.question.dto.QuestionResponseDto;
import com.sparta.willbe.question.model.Question;
import com.sparta.willbe.question.service.QuestionRandomService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
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
    @ApiOperation(value = "카테고리의 랜덤 질문 불러오기")
    @ApiImplicitParam(name = "Authorization", value = "token", dataTypeClass = String.class, paramType = "header", example = "Bearer access_token")
    @GetMapping("/api/questions/{categoryName}")
    public ResponseEntity<QuestionResponseDto> getRandomQuestionFromCategory(@PathVariable String categoryName) {
        log.info("READ RANDOM QUESTION OF " + categoryName);

        boolean isFilterValid = EnumUtils.isValidEnum(CategoryEnum.class, categoryName);
        if (isFilterValid == false) {
            throw new PaginationCategoryInvalidException();
        }

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
