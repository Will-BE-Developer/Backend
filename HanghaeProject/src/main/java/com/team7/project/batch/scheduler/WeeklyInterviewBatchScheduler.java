package com.team7.project.batch.scheduler;


import com.team7.project.batch.config.WeeklyInterviewBatchConfig;
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
public class WeeklyInterviewBatchScheduler {

    private final JobLauncher jobLauncher;
    private final WeeklyInterviewBatchConfig weeklyInterviewBatchConfig;

//    시간 마다 실행
    @Scheduled(cron = "0 0 * * * *")
    public void runJob() {
        Map<String, JobParameter> configMap = new HashMap<>();
        configMap.put("time", new JobParameter((System.currentTimeMillis())));
        JobParameters jobParameters = new JobParameters(configMap);

        try {
            jobLauncher.run(weeklyInterviewBatchConfig.jobWeeklyInterview(), jobParameters);
        } catch (JobExecutionAlreadyRunningException |
                JobInstanceAlreadyCompleteException |
                JobParametersInvalidException |
                JobRestartException e) {
            log.error(e.getMessage());
        }
    }

}
