package com.dev.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableMongoAuditing
@EnableScheduling
@Configuration
public class Application {

	@Value("${mailing.message.quantity}")
	private int mailingMessageQuantity;
	@Value("${mailing.threads.amount}")
	private int mailingThreadsAmount;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(mailingThreadsAmount);
        executor.setQueueCapacity(mailingMessageQuantity*mailingThreadsAmount);
        return executor;
    }
}
