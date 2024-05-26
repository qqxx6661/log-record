package cn.monitor4all.logRecord.thread;

import cn.monitor4all.logRecord.configuration.LogRecordProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 默认线程池提供者
 */
@Slf4j
public class DefaultThreadPoolProvider implements ThreadPoolProvider {

    private final LogRecordProperties logRecordProperties;
    private static final ThreadFactory THREAD_FACTORY = new CustomizableThreadFactory("log-record-");


    public DefaultThreadPoolProvider(LogRecordProperties logRecordProperties) {
        this.logRecordProperties = logRecordProperties;
    }

    @Override
    public ThreadPoolExecutor buildLogRecordThreadPool() {
        log.info("LogRecordThreadPool init poolSize [{}]", logRecordProperties.getThreadPool().getPoolSize());
        int poolSize = logRecordProperties.getThreadPool().getPoolSize();
        return new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024), THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());
    }


}
