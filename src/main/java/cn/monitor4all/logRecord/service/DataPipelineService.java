package cn.monitor4all.logRecord.service;

import cn.monitor4all.logRecord.bean.LogDTO;

/**
 * 消息管道处理接口
 * 非SPI接口，请勿手动实现该接口
 */
public interface DataPipelineService {

    /**
     * 通过消息管道创建日志
     * @param logDTO 日志实体
     * @return 是否成功创建日志
     */
    boolean createLog(LogDTO logDTO);

}
