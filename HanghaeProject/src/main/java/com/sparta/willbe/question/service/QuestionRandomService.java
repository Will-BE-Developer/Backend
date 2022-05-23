package com.sparta.willbe.question.service;

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
            throw ErrorMessage.INVALID_PAGINATION_CATEGORY.throwError();
        }

    }

//    need refactoring, just test
    @Transactional
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
