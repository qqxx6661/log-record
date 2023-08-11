package cn.monitor4all.logRecord.service;

import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * SPI：操作
 *
 * @author wuchao
 * @date 2023-08-11 10:26:02
 */
public interface IOperatorService {

    /**
     * 操作请求属性
     *
     * @param servletRequestAttributes servlet请求属性
     * @throws Exception 异常
     */
    void operatorRequestAttributes(ServletRequestAttributes servletRequestAttributes) throws Exception;

}
