package com.sparta.willbe.batch.config;


import com.sparta.willbe.batch.repository.TopCategoriesRepository;
import com.sparta.willbe.batch.tables.TopCategories;
import com.sparta.willbe.category.model.CategoryEnum;
import com.sparta.willbe.interview.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class TopCategoriesBatchConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final InterviewRepository interviewRepository;
    private final TopCategoriesRepository batchTopCategoriesRepository;

    @Bean
    public Job jobTopCategoies(){
        Job job = jobBuilderFactory.get("jobTopCategoies")
                .start(stepTopCategories())
                .build();
        return job;
    }

    //calculate topcategories from Interview entity
    //save topcategories to topcategories entity
    @Bean
    public Step stepTopCategories(){
        return stepBuilderFactory.get("stepTopCategories")
                .tasklet((contribution, chunkContext)->{
                    System.out.println("QUERY TESTING ::::");

                    //인터뷰를 조회해서 가장 인기가 많은 여섯개의 카테고리를 가져온다
                    List<CategoryEnum> topSix = interviewRepository.findCategoriesOrderedByCategoryCount(PageRequest.of(0,6));
                    log.info("category Eunm : {}",topSix);

                    //카테고리를 등록한다.
                    for(CategoryEnum category : topSix){
                        log.info("topSix Eunm : {}",category.name());
                        TopCategories batch_topCategories = new TopCategories(category);
                        batchTopCategoriesRepository.save(batch_topCategories);
                    };
                     return RepeatStatus.FINISHED;

                })
                .build();
    }
}
