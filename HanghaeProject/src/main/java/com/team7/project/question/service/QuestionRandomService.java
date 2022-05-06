package com.team7.project.question.service;

import com.team7.project.category.model.Category;
import com.team7.project.category.model.CategoryEnum;
import com.team7.project.category.repository.CategoryRepository;
import com.team7.project.question.dto.QuestionRequestDto;
import com.team7.project.question.model.Question;
import com.team7.project.question.repostitory.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class QuestionRandomService {
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;

    public Question getRandomQuestion(CategoryEnum categoryEnum){
        List<Question> questions = questionRepository.findAllByCategory_categoryName(categoryEnum);
//        need Refactoring
        if (questions.isEmpty()){
            init();
            questions = questionRepository.findAllByCategory_categoryName(categoryEnum);
        }
        return questions.get((int)(Math.random()*(questions.size()-1)));
    }

    @Transactional
    public Question saveQuestion(QuestionRequestDto questionRequestDto){
        Category category = categoryRepository.findByCategoryName(questionRequestDto.getCategory()).orElseThrow(
//                need exception refactoring
                RuntimeException::new
        );

        Question question = questionRepository.save(new Question(questionRequestDto.getContents(), questionRequestDto.getReference(), category));

        category.addQuestion(question);

        return question;
    }

//    need refactoring, just test
    @Transactional
    public void init(){
        for(int i = 1; i < 13; ++i){
            for (int j = 1; j< 10; ++j){
                saveQuestion(new QuestionRequestDto("DummyQuestion"+ i +"-"+ j , "test", CategoryEnum.valueOf("DUMMY"+i)));
            }
        }
    }



}
