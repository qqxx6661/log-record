package cn.monitor4all.logRecord.service;

import cn.monitor4all.logRecord.bean.LogDTO;

public interface IOperationLogGetService {

    /**
     * 自定义日志监听
     * @param logDTO 日志传输实体
     */
    void createLog(LogDTO logDTO);

}
