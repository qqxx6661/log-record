package cn.monitor4all.logRecord.springboot3.test.service;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.service.IOperationLogGetService;
import cn.monitor4all.logRecord.springboot3.test.utils.TestHelper;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestComponent;

@Slf4j
@TestComponent
@ConditionalOnProperty(name = "test.config", havingValue = "customLog")
public class OperationLogCustomLogGetService implements IOperationLogGetService {

    @Override
    public boolean createLog(LogDTO logDTO) throws Exception {
        log.info("logDTO: [{}]", JSON.toJSONString(logDTO));

        if ("testBuildLogRequest".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testBuildLogRequest", logDTO);
            TestHelper.releaseLock("testBuildLogRequest");
        }

        return true;
    }
}
