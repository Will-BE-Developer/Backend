package com.sparta.willbe.question.service;

import com.sparta.willbe._global.pagination.exception.PaginationCategoryInvalidException;
import com.sparta.willbe.advice.ErrorMessage;
import com.sparta.willbe.category.model.CategoryEnum;
import com.sparta.willbe.question.repostitory.QuestionRepository;
//import com.team7.project.category.repository.CategoryRepository;
import com.sparta.willbe.question.dto.QuestionRequestDto;
import com.sparta.willbe.question.model.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class QuestionRandomService {
    private final QuestionRepository questionRepository;

    @Transactional
    public Question getRandomQuestion(CategoryEnum categoryEnum){
        List<Question> questions = questionRepository.findAllByCategory(categoryEnum);
        questions = questionRepository.findAllByCategory(categoryEnum);
        return questions.get((int)(Math.random()*(questions.size())));
    }
}
