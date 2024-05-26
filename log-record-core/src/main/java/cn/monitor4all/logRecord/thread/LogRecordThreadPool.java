package cn.monitor4all.logRecord.thread;

import cn.monitor4all.logRecord.configuration.LogRecordProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
@ConditionalOnProperty(name = "log-record.thread-pool.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({LogRecordProperties.class})
public class LogRecordThreadPool {

    private final ThreadPoolExecutor logRecordPoolExecutor;


    /**
     * 操作日志主逻辑线程池
     * 提供顺序：用户传入线程池 优先于 通过配置文件创建的默认线程池
     */
    public LogRecordThreadPool(LogRecordProperties logRecordProperties, ApplicationContext applicationContext) {
        ThreadPoolProvider threadPoolProvider = applicationContext.getBeanProvider(ThreadPoolProvider.class)
                .getIfUnique(() -> new DefaultThreadPoolProvider(logRecordProperties));
        this.logRecordPoolExecutor = threadPoolProvider.buildLogRecordThreadPool();
    }

    public ThreadPoolExecutor getLogRecordPoolExecutor() {
        return logRecordPoolExecutor;
    }
}
