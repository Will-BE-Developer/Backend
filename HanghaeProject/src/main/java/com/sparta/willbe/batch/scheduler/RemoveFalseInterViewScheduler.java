package com.sparta.willbe.batch.scheduler;

import com.sparta.willbe.batch.config.RemoveFalseInterviewConfig;
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
public class RemoveFalseInterViewScheduler {
    private final JobLauncher jobLauncher;
    private final RemoveFalseInterviewConfig removeFalseInterviewConfig;

    //매일 새벽한시에 expired 된 회원을 remove 하는 작업을 실행한다
    @Scheduled(cron = "0/30 * * * * *")
    public void runJob() {
        Map<String, JobParameter> configMap = new HashMap<>();
        configMap.put("time", new JobParameter((System.currentTimeMillis())));
        JobParameters jobParameters = new JobParameters(configMap);

        try {
            jobLauncher.run(removeFalseInterviewConfig.removeInterviews(), jobParameters);
        } catch (JobExecutionAlreadyRunningException |
                JobInstanceAlreadyCompleteException |
                JobParametersInvalidException |
                JobRestartException e) {
            log.error(e.getMessage());
        }
    }


}
