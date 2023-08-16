package cn.monitor4all.logRecord.service;

import cn.monitor4all.logRecord.bean.LogDTO;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * SPI：操作
 */
public interface IOperatorService {

    /**
     * 操作请求属性
     *
     * @param servletRequestAttributes servlet请求属性
     * @param logDTO                   日志实体
     * @throws Exception 异常
     */
    void operatorServletRequestAttributes(ServletRequestAttributes servletRequestAttributes, LogDTO logDTO) throws Exception;

}