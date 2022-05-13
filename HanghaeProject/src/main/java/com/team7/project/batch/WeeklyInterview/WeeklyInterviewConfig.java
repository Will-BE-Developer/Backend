package com.team7.project.batch.WeeklyInterview;

import com.team7.project.interview.model.Interview;
import com.team7.project.interview.repository.InterviewRepository;
import com.team7.project.weeklyInterview.Repository.WeeklyInterviewRepository;
import com.team7.project.weeklyInterview.model.WeeklyInterview;
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
import org.springframework.data.domain.Sort;

import javax.sql.DataSource;
import java.util.*;

// run param: --spring.batch.job.names=weeklyInterviewJob

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class WeeklyInterviewConfig {

    private final WeeklyInterviewRepository weeklyInterviewRepository;
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

    //step 여러개
//    @JobScope
//    @Bean
//    public Step MigrationStep(
//            //ItemReader listItemReader //interviewReader
//            ListItemReader listItemReader //interviewReader
//            , ItemWriter weeklyInterviewWriter
//            , ItemProcessor weeklyInterviewProcessor) {
//
//        return stepBuilderFactory.get("MigrationStep")
//                .<Interview, WeeklyInterview>chunk(3)
//                //.reader(interviewReader)
//                .reader(listItemReader)
//                .processor(weeklyInterviewProcessor)
//                .writer(weeklyInterviewWriter)
//                .build();
//    }

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
    public Tasklet weeklyInterviewTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("weeklyInterviewTasklet is started.");

            //기존 인터뷰 뱃지 삭제
            List<WeeklyInterview> lastWeeklyInterview = weeklyInterviewRepository.findAll();
            for (WeeklyInterview lastWeeklyInterviewEach : lastWeeklyInterview){
                Interview lastInterview = lastWeeklyInterviewEach.getInterview();
                lastInterview.updateBadge("NONE");
                interviewRepository.save(lastInterview);
            }

            //이번주 면접왕 인터뷰 선정 + (추후 추가: 좋아요 숫자, 동점은 인터뷰 최신순)
            //List<Interview> weeklyInterview = interviewRepository.findWeeklyInterview(PageRequest.of(0,3));
            List<WeeklyInterview> weeklyInterview = weeklyInterviewRepository.findWeeklyInterview(PageRequest.of(0,3));
            log.info("weeklyInterview top 3: {}", weeklyInterview);

            //기존 위클리 면접왕 삭제
            weeklyInterviewRepository.deleteAll();

            //위클리 면접왕 저장
            int ranking = 0;
            for (WeeklyInterview weeklyInterviewTop3 : weeklyInterview){

                ranking ++;

                //인터뷰 뱃지 골드,실버,브론즈 저장
                String[] badge = {"Gold", "Silver", "Bronze"};

                log.info("weeklyInterviewTop{}: {}", ranking, weeklyInterviewTop3.getInterview().getId());
                WeeklyInterview weeklyInterviewEach = new WeeklyInterview(weeklyInterviewTop3, badge[ranking-1]);
                weeklyInterviewRepository.save(weeklyInterviewEach);

                //인터뷰 뱃지 골드,실버,브론즈 저장
                Interview interview = weeklyInterviewTop3.getInterview();
                interview.updateBadge(badge[ranking-1]);
                interviewRepository.save(interview);

            }



           return RepeatStatus.FINISHED;
        };
    }

    //step 여러개

//    @StepScope
//    @Bean
//    public RepositoryItemReader<Interview> interviewReader() {
//        List<Interview> findWeeklyInterview = new ArrayList();
//        return new RepositoryItemReaderBuilder<Interview>()
//                .name("interviewReader")
//                .repository(interviewRepository)
//                .methodName("findWeeklyInterview")
//                .pageSize(3)
//                .arguments(PageRequest.of(0,3))   // 파라미터
//                .sorts(Collections.singletonMap("scrap_count", Sort.Direction.DESC))
//                .build();
//    }


//    @StepScope
//    @Bean
//    //public ItemReader<Interview> listItemReader() {
//    public ListItemReader<Interview> listItemReader() {
//        List<Interview> list = (List<Interview>) interviewRepository.findWeeklyInterview(PageRequest.of(0,3));
//        return new ListItemReader<>(list);
//    }

//    @StepScope
//    @Bean
//    public ItemProcessor<Interview, WeeklyInterview> weeklyInterviewProcessor() {
//        return new ItemProcessor<Interview, WeeklyInterview>() {
//            @Override
//            public WeeklyInterview process(Interview top3) throws Exception {
//                return new WeeklyInterview(top3);
//            }
//        };
//    }
//
//    @StepScope
//    @Bean
//    public RepositoryItemWriter<WeeklyInterview> weeklyInterviewWriter() {
//        return new RepositoryItemWriterBuilder<WeeklyInterview>()
//                .repository(weeklyInterviewRepository)
//                .methodName("save")
//                .build();
//    }
}

