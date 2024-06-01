package cn.monitor4all.logRecord.springboot.test;


import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.bean.LogRequest;
import cn.monitor4all.logRecord.springboot.LogRecordAutoConfiguration;
import cn.monitor4all.logRecord.springboot.test.service.OperatorIdGetService;
import cn.monitor4all.logRecord.springboot.test.service.TestService;
import cn.monitor4all.logRecord.springboot.test.utils.TestHelper;
import cn.monitor4all.logRecord.util.OperationLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

/**
 * 单元测试：自定义线程池
 */
@Slf4j
@SpringBootTest
@ContextConfiguration(classes = {
        LogRecordAutoConfiguration.class,
        OperatorIdGetService.class,
        TestService.class,})
@PropertySource("classpath:testCustomLog.properties")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class OperationLogCustomLogTest {

    @Test
    public void testBuildLogRequest() {
        TestHelper.addLock("testBuildLogRequest");
        Date date = new Date();
        LogRequest logRequest = LogRequest.builder()
                .bizId("testBizId")
                .bizType("testBuildLogRequest")
                .exception("testException")
                .operateDate(date)
                .success(true)
                .msg("testMsg")
                .tag("testTag")
                .returnStr("testReturnStr")
                .executionTime(0L)
                .extra("testExtra")
                .operatorId("testOperatorId")
                .build();
        OperationLogUtil.log(logRequest);
        TestHelper.await("testBuildLogRequest");
        LogDTO logDTO = TestHelper.getLogDTO("testBuildLogRequest");

        Assertions.assertEquals("testBizId", logDTO.getBizId());
        Assertions.assertEquals("testBuildLogRequest", logDTO.getBizType());
        Assertions.assertEquals("testException", logDTO.getException());
        Assertions.assertEquals(date, logDTO.getOperateDate());
        Assertions.assertEquals(true, logDTO.getSuccess());
        Assertions.assertEquals("testMsg", logDTO.getMsg());
        Assertions.assertEquals("testTag", logDTO.getTag());
        Assertions.assertEquals("testReturnStr", logDTO.getReturnStr());
        Assertions.assertEquals(0L, logDTO.getExecutionTime());
        Assertions.assertEquals("testExtra", logDTO.getExtra());
        Assertions.assertEquals("testOperatorId", logDTO.getOperatorId());
    }

}
