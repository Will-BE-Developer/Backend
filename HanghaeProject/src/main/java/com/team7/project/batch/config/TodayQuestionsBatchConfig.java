package com.team7.project.batch.config;

import com.team7.project.batch.BATCH_repository.BATCH_TodayQuestionRepository;
import com.team7.project.batch.tables.BATCH_TodayQuestion;
import com.team7.project.batch.tables.BATCH_TopCategories;
import com.team7.project.category.model.CategoryEnum;
import com.team7.project.question.model.Question;
import com.team7.project.question.repostitory.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class TodayQuestionsBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    @Autowired
    private final QuestionRepository questionRepository;
    @Autowired
    private final BATCH_TodayQuestionRepository batch_todayQuestionRepository;

    @Bean
    public Job jobTodayQuestions(){
        Job job = jobBuilderFactory.get("jobTodayQuestions")
                .start(stepTodayQuestions())
                .build();
        return job;
    }

    @Bean
    public Step stepTodayQuestions(){
        return stepBuilderFactory.get("stepTodayQuestions")
                .tasklet((contribution, chunkContext)->{
                    batch_todayQuestionRepository.deleteAll();
                    List<CategoryEnum> categories = selectCategories();
                    for(CategoryEnum cat: categories){
                        List<Question> findQuestions = questionRepository.findAllByCategoryAndIsShow(cat,false);
                       int num =  (int)(Math.random() * findQuestions.size());
                      Question selected = findQuestions.get(num);
                      batch_todayQuestionRepository.save(new BATCH_TodayQuestion(selected.getId()));
                    }
                    return RepeatStatus.FINISHED;

                })
                .build();
    }

    public List<CategoryEnum> selectCategories(){
        List getEnum = Arrays.asList(CategoryEnum.values());
        int len = CategoryEnum.values().length;
        List<CategoryEnum> selected = new ArrayList();
        int ran ;
        while(selected.size() <3) {
            ran = (int) (Math.random() * len);
            if (!selected.contains(getEnum.get(ran))) {
                selected.add((CategoryEnum) getEnum.get(ran));
            }
        }
        return selected;
    }

}
