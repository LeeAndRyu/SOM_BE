package com.blog.som.global.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

  @Override
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(3); //기본적으로 실행을유지하는 스레드 수
    executor.setMaxPoolSize(10); //최대 스레드 수
    executor.setQueueCapacity(100); //작업 큐의 용량
    executor.setThreadNamePrefix("AsyncExecutor-");
    executor.initialize();
    return executor;
  }
}
