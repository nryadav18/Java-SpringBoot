package com.course.scheduling.config;

import java.util.Arrays;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/*
 * Turns ON @Async and defines the THREAD POOL that runs @Async methods.
 *
 * WHY a custom pool?
 *   - We decide how many threads may run in parallel (core / max size).
 *   - We decide how many tasks may WAIT in the queue (queue capacity) - a bounded queue
 *     protects us from running out of memory when too much work arrives.
 *   - Our own thread-name prefix ("async-") makes the log easy to read.
 *
 * Implementing AsyncConfigurer makes this pool the DEFAULT for every @Async method,
 * and lets us register a handler for exceptions thrown by @Async methods that return void.
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class);

    @Bean(name = "appTaskExecutor")
    public ThreadPoolTaskExecutor appTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);        // threads kept alive all the time
        executor.setMaxPoolSize(6);         // extra threads are created ONLY when the queue is full
        executor.setQueueCapacity(50);      // tasks waiting for a free thread
        executor.setThreadNamePrefix("async-");
        // on shutdown, let running tasks finish (max 10 s) instead of killing them
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(10);
        executor.initialize();
        return executor;
    }

    /** The executor used by @Async when no name is given. */
    @Override
    public Executor getAsyncExecutor() {
        return appTaskExecutor();   // inside @Configuration this returns the SAME singleton bean
    }

    /**
     * An @Async method that returns void has no caller waiting for it, so an exception
     * would otherwise be lost. This handler at least LOGS it.
     * (Methods returning CompletableFuture pass the exception to the caller instead.)
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (exception, method, params) ->
                log.error("@Async method {}({}) failed: {}",
                        method.getName(), Arrays.toString(params), exception.getMessage());
    }
}
