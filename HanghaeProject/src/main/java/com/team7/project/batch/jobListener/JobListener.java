package com.team7.project.batch.jobListener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class JobListener implements JobExecutionListener {

    private static String BEFORE_MESSAGE = "{} job이 실행됩니다.";

    private static String AFTER_MESSAGE = "{} job이 끝났습니다. (상태: {})";

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info(BEFORE_MESSAGE, jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info(AFTER_MESSAGE,
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus());

        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("주간 면접왕 배치 작업이 실패되었습니다.");
        }
    }
}
