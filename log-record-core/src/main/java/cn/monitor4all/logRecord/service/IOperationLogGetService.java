package cn.monitor4all.logRecord.service;

import cn.monitor4all.logRecord.bean.LogDTO;

/**
 * SPI：操作日志本地处理
 */
public interface IOperationLogGetService {

    /**
     * 自定义日志监听
     * @param logDTO 日志传输实体
     */
    boolean createLog(LogDTO logDTO) throws Exception;

}
