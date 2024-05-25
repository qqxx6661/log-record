package cn.monitor4all.logRecord.springboot.test.service;

import cn.monitor4all.logRecord.thread.ThreadPoolProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@TestComponent
@ConditionalOnProperty(name = "test.config", havingValue = "customThreadPool")
public class CustomThreadPoolProvider implements ThreadPoolProvider {

    private static ThreadPoolExecutor EXECUTOR;

    private static final ThreadFactory THREAD_FACTORY = new CustomizableThreadFactory("log-record-");


    private CustomThreadPoolProvider() {
        log.info("CustomThreadPoolProvider init");
        EXECUTOR = new ThreadPoolExecutor(3, 3, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100), THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public ThreadPoolExecutor buildLogRecordThreadPool() {
        return EXECUTOR;
    }
}
