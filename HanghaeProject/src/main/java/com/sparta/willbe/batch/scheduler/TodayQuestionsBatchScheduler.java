package com.sparta.willbe.batch.scheduler;


import com.sparta.willbe.batch.config.TodayQuestionsBatchConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TodayQuestionsBatchScheduler {
    private final JobLauncher jobLauncher;
    private final TodayQuestionsBatchConfig todayQuestionsBatchConfig;
//    매일 3시에 실행
//    @Scheduled(cron = "0 0 3 * * *")
//    매 50초마다 실행 --테스트용
//    @Scheduled(cron = "0/50 * * * * *")
    public void runJob() {
        Map<String, JobParameter> configMap = new HashMap<>();
        configMap.put("time", new JobParameter((System.currentTimeMillis())));
        JobParameters jobParameters = new JobParameters(configMap);

        try {
            jobLauncher.run(todayQuestionsBatchConfig.jobTodayQuestions(), jobParameters);
        } catch (JobExecutionAlreadyRunningException |
                JobInstanceAlreadyCompleteException |
                JobParametersInvalidException |
                JobRestartException e) {
            log.error(e.getMessage());
        }
    }
}
