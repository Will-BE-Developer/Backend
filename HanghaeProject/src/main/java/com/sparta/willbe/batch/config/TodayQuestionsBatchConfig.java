package com.sparta.willbe.batch.config;

import com.sparta.willbe.batch.repository.TodayQuestionRepository;
import com.sparta.willbe.category.model.CategoryEnum;
import com.sparta.willbe.question.model.Question;
import com.sparta.willbe.question.repostitory.QuestionRepository;
import com.sparta.willbe.batch.tables.BATCH_TodayQuestion;
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

import java.util.*;

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
    private final TodayQuestionRepository batch_todayQuestionRepository;

    @Bean
    public Job jobTodayQuestions() {
        Job job = jobBuilderFactory.get("jobTodayQuestions")
                .start(updateCheckAndDo())
                .next(stepTodayQuestions())
                .build();
        return job;
    }

    @Bean
    public Step updateCheckAndDo() {
        return stepBuilderFactory.get("updateCheckAndDo")
                .tasklet((contribution, chunkContext) -> {
                    if(updateCheck()){
                        updateTrue();
                    }
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
    @Bean
    public Step stepTodayQuestions() {
        return stepBuilderFactory.get("stepTodayQuestions")
                .tasklet((contribution, chunkContext) -> {
                    //한번 돌때마다 데이터 베이스를 다 지워준다
                    batch_todayQuestionRepository.deleteAll();
                    //카테고리를 먼저 생성 한다
                    Map<CategoryEnum, List<Question>> categories = selectCategories();
                    //카테고리 안에서 isshow 가 false 인 문제를 찾아서 랜덤 질문을 뽑는다.
                    pickQuestions(categories);
                    return RepeatStatus.FINISHED;

                })
                .build();
    }

    //랜덤으로 카테고리를 추출한다
    //랜덤으로 고른 카테고리에 고를 수 있는 질문이 남아있지 않은경우, 다른 카테고리를 랜덤으로 선택한다
    //랜덤으로 3개의 카테고리가 추출되면, 카테고리와 질문 목록을 반환한다.
    public Map<CategoryEnum, List<Question>> selectCategories() {
        List getEnum = Arrays.asList(CategoryEnum.values());
        int len = getEnum.size();
        Map<CategoryEnum, List<Question>> selected = new HashMap<CategoryEnum, List<Question>>();
        int ran;
        while (selected.size() < 3) {
            // 0~len-1 사이의 난수 추출
            ran = (int) (Math.random() * len);
            List<Question> findQuestions = questionRepository.findAllByCategoryAndIsShow((CategoryEnum) getEnum.get(ran), false);
            boolean questionExist = findQuestions.size() > 0;

            if (!selected.containsKey(getEnum.get(ran)) && questionExist) {
                selected.put((CategoryEnum) getEnum.get(ran), findQuestions);
            }
        }
        return selected;
    }

    //카테고리와 질문목록을 파라미터로 받는다.
    //질문목록에서 질문을 추출한다.
    //각각의 카테고리에서 추출한 질문을 batch_today_question 데이터 베이스에 업데이트 시킨다.
    public void pickQuestions(Map<CategoryEnum, List<Question>> categories) {
        categories.forEach((k, v) -> {
            int num = (int) (Math.random() * v.size());
            Question selected = v.get(num);
            selected.setShow(true);
            batch_todayQuestionRepository.save(new BATCH_TodayQuestion(selected.getId()));
        });
    }

    //업데이트가 필요한지 안필요한지 확인해준다
    //QUESTION이 2개(LIMIT_QUESTIONS) 밖에 안남은 카테고리 수가 3개(LIMIT_CATEGORIES)를 초과하면
    //업데이트가 필요하므로 TRUE를 반환한다
    //그 외에는 FALSE를 반환한다.
    public boolean updateCheck() {
        List<Question> findQuestions;
        final int LIMIT_CATEGORIES = 3;
        final int LIMIT_QUESTIONS = 2;
        int underCount = 0;
        int index = 0;
        log.info("UPDATECHECK :: ENTERED");
        while (index < CategoryEnum.values().length && underCount <= LIMIT_CATEGORIES) {
            findQuestions = questionRepository.findAllByCategoryAndIsShow(CategoryEnum.values()[index], false);
            if (findQuestions.size() <= LIMIT_QUESTIONS) {
                underCount++;
            }
            index++;
        }
        if (underCount > LIMIT_CATEGORIES) {
            log.info("UPDATE_NEEDED ::: underCount exceeds LIMIT_CATEGORIES!!");
            log.info(" underCount : {}", underCount);
            return true;
        }
        log.info("UPDATE_NOT_NEEDED ::: underCount is still under LIMIT_CATEGORIES!!");
        log.info(" underCount : {}", underCount);
        return false;
    }


    //question entity의 isshow 항목에 대해서 FALSE로 업데이트를 진행한다.
    public void updateTrue(){
       List<Question> allQuestions = questionRepository.findAll();
       for(Question question : allQuestions){
           question.setShow(false);
       }
    }
}



