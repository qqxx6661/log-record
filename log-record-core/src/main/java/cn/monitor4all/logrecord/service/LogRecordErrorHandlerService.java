package cn.monitor4all.logRecord.service;


/**
 * SPI: 操作日志处理错误兜底处理器
 */
public interface LogRecordErrorHandlerService {

    /**
     * 本地操作日志处理失败处理器
     * 会在日志记录失败后被调用，若配置了重试次数，则超过重试次数后被调用
     */
    default void operationLogGetErrorHandler() {}

    /**
     * 数据管道操作日志处理失败处理器
     * 会在日志记录失败后被调用，若配置了重试次数，则超过重试次数后被调用
     */
    default void dataPipelineErrorHandler() {}

}
