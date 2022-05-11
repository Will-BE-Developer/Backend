package com.team7.project.batch.config;


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

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class WeeklyInterviewBatchConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jobWeeklyInterview(){
        Job job = jobBuilderFactory.get("jobWeeklyInterview")
                .start(stepWeeklyInterview1())
                .next(stepWeeklyInterview2())
                .build();
        return job;
    }

    @Bean
    public Step stepWeeklyInterview1() {
        return stepBuilderFactory.get("stepWeeklyInterview1")
                .tasklet((contribution, chunkContext)->{
                    log.info("STEP: stepWeeklyInterview1");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step stepWeeklyInterview2() {
        return stepBuilderFactory.get("stepWeeklyInterview2")
                .tasklet((contribution, chunkContext)->{
                    log.info("STEP: stepWeeklyInterview2");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }


}
