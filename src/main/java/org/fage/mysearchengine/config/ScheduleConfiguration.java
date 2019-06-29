package org.fage.mysearchengine.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午5:48 2018/5/22
 * @description 定时任务配置类，配置异步定时任务
 **/
@Configuration
@EnableScheduling
public class ScheduleConfiguration implements SchedulingConfigurer, AsyncConfigurer {
    final Logger logger = LoggerFactory.getLogger(ScheduleConfiguration.class);

    /**
     * 异步处理
     */
    @Override
    public Executor getAsyncExecutor() {
        Executor executor = taskScheduler();
        return executor;
    }

    /**
     * 异步任务中异常处理
     *
     * @return
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {
            @Override
            public void handleUncaughtException(Throwable ex, Method method, Object... params) {
                logger.error("==========================" + ex.getMessage() + "=======================", ex);
                logger.error("exception method:" + method.getName());
            }
        };
    }

    /**
     * 异步处理
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        TaskScheduler taskScheduler = taskScheduler();
        scheduledTaskRegistrar.setTaskScheduler(taskScheduler);
    }

    /**
     * 定时任务多线程处理
     */
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler taskScheduler() {
        //创建一个线程池调度器
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        //初始化20个线程
        scheduler.setPoolSize(20);
        //设置线程前缀
        scheduler.setThreadNamePrefix("task-");
        //等待时长
        scheduler.setAwaitTerminationSeconds(60);
        //设置当任务被取消的同时从当前调度器移除的策略
        scheduler.setRemoveOnCancelPolicy(true);
        //当调度器shutdown被调用时等待当前被调度的任务完成
        scheduler.setWaitForTasksToCompleteOnShutdown(true);

        return scheduler;
    }
}
