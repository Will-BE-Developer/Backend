package com.team7.project.batch.WeeklyInterview;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Collections;

@Slf4j
@Component
public class WeeklyInterviewScheduler {

    @Autowired
    private Job weeklyInterviewJob;

    @Autowired
    private JobLauncher jobLauncher;

    //주간 면접왕
    //@Scheduled(cron = "0 0 0 ? * MON")  //Mon 00:00:00
    @Scheduled(cron = "0 0/1 * * * *")
    public void runWeeklyInterviewJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters jobParameters = new JobParameters(
                Collections.singletonMap("requestTime", new JobParameter(System.currentTimeMillis()))
        );
        
        try {
            jobLauncher.run(weeklyInterviewJob, jobParameters);
            log.info("weeklyInterviewJob 배치 실행됨: " + Calendar.getInstance().getTime());
        } catch (JobExecutionAlreadyRunningException |
                JobInstanceAlreadyCompleteException |
                JobParametersInvalidException |
                JobRestartException e) {
            log.error(e.getMessage());
        }
    }
}
