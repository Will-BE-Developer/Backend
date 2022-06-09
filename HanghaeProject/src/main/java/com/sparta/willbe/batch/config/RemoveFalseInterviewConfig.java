package com.sparta.willbe.batch.config;


import com.sparta.willbe.interview.model.Interview;
import com.sparta.willbe.interview.repository.InterviewRepository;
import com.sparta.willbe.user.model.User;
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

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class RemoveFalseInterviewConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    private final InterviewRepository interviewRepository;
    @Bean
    public Job removeInterviews(){
        Job job = jobBuilderFactory.get("removeInterviews")
                .start(findfalseInterviews())
                .build();
        return job;
    }

    @Bean
    public Step findfalseInterviews() {
        log.info("BATCH >> Errored interview deleting is processing...");
        return stepBuilderFactory.get("findfalseInterviews")
                .tasklet((contribution, chunkContext) -> {
                   List<Interview> interviews = interviewRepository.findAllByIsVideoConvertedFalse();
                   for(Interview interview: interviews){
                       if(isErrored(interview.getCreatedAt())){
                           log.debug("This interview seems errored during converted. Delete is processing...");
                           interviewRepository.delete(interview);
                       }else{
                           continue;
                       }
                   }
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    public boolean isErrored(LocalDateTime createdTime){
        return LocalDateTime.now().isAfter(createdTime.plusDays(1));
    }

}
