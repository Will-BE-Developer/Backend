package com.team7.project.batch.config;

import com.team7.project.batch.jobListener.JobListener;
import com.team7.project.interview.model.Interview;
import com.team7.project.interview.repository.InterviewRepository;
import com.team7.project.batch.BATCH_repository.BATCH_WeeklyInterviewRepository;
import com.team7.project.batch.tables.BATCH_WeeklyInterview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;

//import javax.batch.api.listener.JobListener;
import javax.transaction.Transactional;
import java.util.*;

// run param: --spring.batch.job.names=weeklyInterviewJob

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class WeeklyInterviewConfig {

    private final BATCH_WeeklyInterviewRepository weeklyInterviewRepository;
    private final InterviewRepository interviewRepository;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    //private final EntityManagerFactory entityManagerFactory;
    //private final DataSource dataSource;
    //private final ListItemReader listItemReader;

    @Bean
    public Job weeklyInterviewJob(Step MigrationStep) {
        return jobBuilderFactory.get("weeklyInterviewJob")
                .incrementer(new RunIdIncrementer())
                .listener(new JobListener())
                .start(MigrationStep)
                .build();
    }

    //step 한개
    @JobScope
    @Bean
    public Step MigrationStep(Tasklet weeklyInterviewTasklet) {
        return stepBuilderFactory.get("MigrationStep")
                .tasklet(weeklyInterviewTasklet)
                .build();
    }

    @StepScope
    @Bean
    @Transactional
    public Tasklet weeklyInterviewTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("weeklyInterviewTasklet is started.");

            //기존 인터뷰 뱃지 삭제
            List<BATCH_WeeklyInterview> lastWeeklyInterview = weeklyInterviewRepository.findAll();
            for (BATCH_WeeklyInterview lastWeeklyInterviewEach : lastWeeklyInterview){
                Interview lastInterview = lastWeeklyInterviewEach.getInterview();
                lastInterview.updateBadge("NONE");
                interviewRepository.save(lastInterview);
            }

            //이번주 면접왕 인터뷰 선정 + (추후 추가: 좋아요 숫자, 동점은 인터뷰 최신순)
            //List<Interview> weeklyInterview = interviewRepository.findWeeklyInterview(PageRequest.of(0,3));
            List<BATCH_WeeklyInterview> weeklyInterview = weeklyInterviewRepository.findWeeklyInterview(PageRequest.of(0,5));
            log.info("weeklyInterview top 3: {}", weeklyInterview);

            //기존 위클리 면접왕 삭제
            weeklyInterviewRepository.deleteAll();

            //위클리 면접왕 저장
            int ranking = 0;
            for (BATCH_WeeklyInterview weeklyInterviewTop3 : weeklyInterview){

                ranking ++;

                //인터뷰 뱃지 골드,실버,브론즈 저장
                //String[] badge = {"Gold", "Silver", "Bronze"};
                String[] badge = {"1등", "2등", "3등", "4등", "5등"};

                //현재날짜의 지난주
                Date currentDate = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                String month = String.valueOf(calendar.get(Calendar.MONTH)+1);
                String week = String.valueOf(calendar.get(Calendar.WEEK_OF_MONTH)-1);  //지난주
                //String weeklyBadge = month + "월 "+ week + "번째주 " + badge[ranking-1];
                String weeklyBadge = month + "월 "+ week + "번째주 " + ranking + "등";
                System.out.println("weeklyBadge: " + weeklyBadge);

                log.info("weeklyInterviewTop{}: {}", ranking, weeklyInterviewTop3.getInterview().getId());

                BATCH_WeeklyInterview weeklyInterviewEach = new BATCH_WeeklyInterview(weeklyInterviewTop3, badge[ranking-1], weeklyBadge);
                weeklyInterviewEach.setWeeklyBadge(weeklyBadge);
                weeklyInterviewRepository.save(weeklyInterviewEach);

                //인터뷰 뱃지 저장
                Interview interview = weeklyInterviewTop3.getInterview();
                //interview.updateBadge(badge[ranking-1]);
                interview.updateBadge(weeklyBadge);
                interviewRepository.save(interview);
            }

           return RepeatStatus.FINISHED;
        };
    }
}

