package cn.monitor4all.logRecord.thread;


import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池提供者
 */
public interface ThreadPoolProvider {

    /**
     * 提供操作日志处理线程池
     */
    ThreadPoolExecutor buildLogRecordThreadPool();

}
