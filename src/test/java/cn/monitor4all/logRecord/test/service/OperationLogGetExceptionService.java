package cn.monitor4all.logRecord.test.service;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.exception.LogRecordException;
import cn.monitor4all.logRecord.service.IOperationLogGetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestComponent;

@Slf4j
@TestComponent
@ConditionalOnProperty(name = "test.config", havingValue = "exception")
public class OperationLogGetExceptionService implements IOperationLogGetService {

    @Override
    public boolean createLog(LogDTO logDTO) throws Exception {
        log.error("try to mock error");
        throw new LogRecordException("mock error", new Throwable());
    }
}
