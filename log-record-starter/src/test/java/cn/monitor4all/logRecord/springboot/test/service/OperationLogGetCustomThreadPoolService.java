package cn.monitor4all.logRecord.springboot.test.service;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.service.IOperationLogGetService;
import cn.monitor4all.logRecord.springboot.test.utils.TestHelper;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestComponent;

@Slf4j
@TestComponent
@ConditionalOnProperty(name = "test.config", havingValue = "customThreadPool")
public class OperationLogGetCustomThreadPoolService implements IOperationLogGetService {

    @Override
    public boolean createLog(LogDTO logDTO) throws Exception {
        log.info("logDTO: [{}]", JSON.toJSONString(logDTO));

        if ("testCustomThreadPool".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testCustomThreadPool", logDTO);
            TestHelper.releaseLock("testCustomThreadPool");
        }

        return true;
    }
}
