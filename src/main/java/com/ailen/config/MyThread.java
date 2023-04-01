package com.ailen.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@Slf4j
public class MyThread {

    @Bean("thread1")
    public ThreadPoolTaskExecutor executor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        final int processors = Runtime.getRuntime().availableProcessors();
        /**
         *核心线程1
         * 最大线程数5
         * 允许线程空闲时间（单位：默认为秒）
         * 缓冲队列数
         * 线程池名前缀
         * 线程池对拒绝任务的处理策略
         */
        executor.setCorePoolSize(processors);
        executor.setMaxPoolSize(processors*2+1);
        executor.setKeepAliveSeconds(30);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AilenThread");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
//        初始化
        executor.initialize();
        return executor;
    }

}
