package com.team7.project;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@EnableBatchProcessing
@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
//@EnableAspectJAutoProxy(proxyTargetClass = true)  //NoSuchMethodException: com.sun.proxy
public class ProjectApplication {
	static {
		//amazon EC2가 아닌 환경(로컬)에서 테스트시 Failed to connect to service endpoint 에러 방지용
		System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
	}

	@PostConstruct
	void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

}
