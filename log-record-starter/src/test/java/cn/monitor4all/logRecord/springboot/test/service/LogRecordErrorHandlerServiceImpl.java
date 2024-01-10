package cn.monitor4all.logRecord.springboot.test.service;

import cn.monitor4all.logRecord.service.LogRecordErrorHandlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestComponent;

@Slf4j
@TestComponent
public class LogRecordErrorHandlerServiceImpl implements LogRecordErrorHandlerService {


    @Override
    public void operationLogGetErrorHandler() {
        log.error("operation log get service error reached max retryTimes!");
    }

    @Override
    public void dataPipelineErrorHandler() {
        log.error("data pipeline send log error reached max retryTimes!");
    }
}
