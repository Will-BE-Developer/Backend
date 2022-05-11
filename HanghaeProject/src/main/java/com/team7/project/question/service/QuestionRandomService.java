package com.team7.project.question.service;

import com.team7.project.advice.RestException;
import com.team7.project.category.model.CategoryEnum;
//import com.team7.project.category.repository.CategoryRepository;
import com.team7.project.question.dto.QuestionRequestDto;
import com.team7.project.question.model.Question;
import com.team7.project.question.repostitory.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    public Question getRandomQuestion(CategoryEnum categoryEnum){
        List<Question> questions = questionRepository.findAllByCategory(categoryEnum);
//        need Refactoring
        if (questions.isEmpty()){
            init();
            questions = questionRepository.findAllByCategory(categoryEnum);
        }
        return questions.get((int)(Math.random()*(questions.size())));
    }

    @Transactional
    public Question saveQuestion(QuestionRequestDto questionRequestDto){
        Question question;
        List<CategoryEnum> categoryEnums = Arrays.asList(CategoryEnum.values());

        //compare with String value
        if(categoryEnums.contains(CategoryEnum.valueOf(questionRequestDto.getCategory()))){

            String contents = questionRequestDto.getContents();
            String reference = questionRequestDto.getReference();
            CategoryEnum category = CategoryEnum.valueOf(questionRequestDto.getCategory());

            return questionRepository.save(new Question(contents, reference, category));
        }
        else
        {
            log.error(questionRequestDto.getCategory() + "라는 잘못된 카테고리를 입력했습니다.");
            throw new RestException(HttpStatus.BAD_REQUEST, "잘못된 카테고리를 입력했습니다.");
        }

    }

//    need refactoring, just test
    public void init(){
        List<CategoryEnum> categoryEnums = Arrays.asList(CategoryEnum.values());
        for(CategoryEnum categoryEnum: categoryEnums){
            String categoryName = categoryEnum.name();
            for (int j = 1; j< 10; ++j){
                saveQuestion(new QuestionRequestDto(categoryName +"-"+ j , "reference test", categoryName));
            }
        }
    }

}
