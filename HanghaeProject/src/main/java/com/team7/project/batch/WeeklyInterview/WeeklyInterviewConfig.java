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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManagerFactory;
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
    private final EntityManagerFactory entityManagerFactory;
    private final DataSource dataSource;

    @Bean
    public Job weeklyInterviewJob(Step MigrationStep) {
        return jobBuilderFactory.get("weeklyInterviewJob")
                .incrementer(new RunIdIncrementer())
                .listener(new JobListener())
                .start(MigrationStep)
                .build();
    }

    @JobScope
    @Bean
    public Step MigrationStep(
            ItemReader interviewReader //interviewReader
            , ItemWriter weeklyInterviewWriter
            , ItemProcessor weeklyInterviewProcessor) {

        return stepBuilderFactory.get("MigrationStep")
                .<Interview, WeeklyInterview>chunk(3)
                .reader(interviewReader)
                .processor(weeklyInterviewProcessor)
                .writer(weeklyInterviewWriter)
                .build();
    }

    @StepScope
    @Bean
    public RepositoryItemReader<Interview> interviewReader() {
        List<Interview> findWeeklyInterview = new ArrayList();
        return new RepositoryItemReaderBuilder<Interview>()
                .name("interviewReader")
                .repository(interviewRepository)
                .methodName("findWeeklyInterview")
                .pageSize(3)
                .arguments(Arrays.asList(PageRequest.of(0,3)))   // 파라미터
                .sorts(Collections.singletonMap("scrap_count", Sort.Direction.DESC))
                .build();
    }

//
//        RepositoryItemReader<Interview> reader = new RepositoryItemReader<>();
//        reader.setName("interviewReader");
//        reader.setRepository(interviewRepository);
//        reader.setPageSize(3);
//        reader.setMethodName("findWeeklyInterview");
//        reader.setArguments(Arrays.asList());
//        reader.setSort(Collections.singletonMap("scrap_count", Sort.Direction.DESC));
//
//        return reader;
//    }

//    String query = "SELECT i.id as interview_id, i.user_id, i.question_id, s.scrap_count FROM interview i " +
//        "    inner JOIN ( " +
//        "        SELECT interview_id, count(interview_id) as scrap_count FROM scrap " +
//        "WHERE scrap.created_at BETWEEN DATE_ADD(NOW(), INTERVAL -1 WEEK ) AND NOW() " +
//        "group by interview_id " +
//        "order by scrap_count DESC LIMIT 3  " +
//        "    ) s " +
//        "    ON i.id IN (s.interview_id)";

//    @StepScope
//    @Bean
//    public JpaPagingItemReader<Interview> interviewReader() {
//        return new JpaPagingItemReaderBuilder<Interview>()
//                .name("interviewReader")
//                .entityManagerFactory(entityManagerFactory)
//                .pageSize(3)
//                .queryString(query)
//                .build();
//    }

    //https://docs.spring.io/spring-batch/docs/current/reference/html/readersAndWriters.html
//    @StepScope
//    @Bean
//    public JdbcCursorItemReader<Interview> interviewReader() {
//        return new JdbcCursorItemReaderBuilder<Interview>()
//                .dataSource(this.dataSource)
//                .name("creditReader")
//                .sql(query)
//                .rowMapper(new weeklyInterviewRowMapper())
//                .build();
//    }

//    @StepScope
//    @Bean
//    public ItemReader<Interview> interviewReader() {
//        List<Interview> list = interviewRepository.findWeeklyInterview(PageRequest.of(0,3));
//        return new interviewReader<>(list);
//    }

    @StepScope
    @Bean
    public ItemProcessor<Interview, WeeklyInterview> weeklyInterviewProcessor() {
        return new ItemProcessor<Interview, WeeklyInterview>() {
            @Override
            public WeeklyInterview process(Interview top3) throws Exception {
                return new WeeklyInterview(top3);
            }
        };
    }

    @StepScope
    @Bean
    public RepositoryItemWriter<WeeklyInterview> weeklyInterviewWriter() {
        return new RepositoryItemWriterBuilder<WeeklyInterview>()
                .repository(weeklyInterviewRepository)
                .methodName("save")
                .build();
    }
}

