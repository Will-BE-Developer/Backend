package com.sparta.willbe.batch.config;

import com.sparta.willbe.interview.repository.InterviewRepository;
import com.sparta.willbe.user.model.User;
import com.sparta.willbe.user.repository.UserRepository;
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
public class RemoveUserDeleteConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    private final UserRepository userRepository;

    @Bean
    public Job removeUser(){
        Job job = jobBuilderFactory.get("jobTodayQuestions")
                .start(findDeleteUserExpired())
                .build();
        return job;
    }

    @Bean
    public Step findDeleteUserExpired() {
        log.info("BATCH >> user deleting is processing...");
        return stepBuilderFactory.get("findDeleteUserExpired")
                .tasklet((contribution, chunkContext) -> {
                    List<User> users = userRepository.findAllByIsDeletedTrue();
                   if(users.size() > 0){
                       log.debug("Deleted user size is greater then 0. Start validating expired date...");
                       for( User user : users ){
                           if(check6MonthExpired(user.getModifiedAt())){
                               log.debug("deleted before 6 month  >> removing from database...");
                               userRepository.deleteById(user.getId());
                           }else{
                               continue;
                           }
                       }
                   }

                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    //현재 시간이 expired 시간을 넘겼을 경우 true를 반환
    public boolean check6MonthExpired(LocalDateTime deletedDate){
        return LocalDateTime.now().isAfter(deletedDate.plusMonths(6));
    }

}


