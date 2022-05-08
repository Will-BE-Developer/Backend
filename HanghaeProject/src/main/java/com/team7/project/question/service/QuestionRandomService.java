package com.team7.project.question.service;

import com.team7.project.category.model.CategoryEnum;
//import com.team7.project.category.repository.CategoryRepository;
import com.team7.project.question.dto.QuestionRequestDto;
import com.team7.project.question.model.Question;
import com.team7.project.question.repostitory.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;


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
            //do something
            String contents = questionRequestDto.getContents();
            String reference = questionRequestDto.getReference();
            CategoryEnum category = CategoryEnum.valueOf(questionRequestDto.getCategory());
            return questionRepository.save(new Question(contents, reference, category));
        }
        else
        {
            // need exception refactoring
            throw new IllegalArgumentException();
        }
//        Category category = categoryRepository.findByCategoryName(questionRequestDto.getCategory()).orElseThrow(
////                need exception refactoring
//                RuntimeException::new
//        );

//        category.addQuestion(question);
    }

//    need refactoring, just test
    public void init(){
        for(int i = 1; i < 13; ++i){
            for (int j = 1; j< 10; ++j){
                saveQuestion(new QuestionRequestDto("DummyQuestion"+ i +"-"+ j , "test", "DUMMY"+i));
            }
        }
    }

}
