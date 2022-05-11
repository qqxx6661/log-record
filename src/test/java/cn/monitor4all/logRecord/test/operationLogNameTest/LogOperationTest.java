package cn.monitor4all.logRecord.test.operationLogNameTest;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.configuration.LogRecordAutoConfiguration;
import cn.monitor4all.logRecord.service.IOperationLogGetService;
import cn.monitor4all.logRecord.test.operationLogNameTest.service.OperationLogGetService;
import cn.monitor4all.logRecord.test.operationLogNameTest.service.TestService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ContextConfiguration;

/**
 * 单元测试
 */
@Slf4j
@SpringBootTest
@ContextConfiguration(classes = {
        LogRecordAutoConfiguration.class,
        LogOperationTest.CustomFuncTestOperationLogGetService.class,
        OperationLogGetService.class,
        TestService.class,})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class LogOperationTest {

    @Autowired
    private TestService testService;

    @Test
    public void logRecordFuncTest() {
        testService.testBizId("1");
        testService.testReturnStr();
        testService.testRecordResult();
        testService.testReturnObject();
        try {
            testService.testException();
        } catch (Exception ignored) {}
        testService.testMsgAndExtra("李四", new TestService.TestClass(1, "name"));
        testService.testCustomFunc();
        testService.testOperatorId("001");
        testService.testExecuteBeforeFunc();
    }

    @TestComponent
    public static class CustomFuncTestOperationLogGetService implements IOperationLogGetService {

        @Override
        public void createLog(LogDTO logDTO) {
            log.info("logDTO: [{}]", JSON.toJSONString(logDTO));

            if ("testBizIdWithSpEL".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getBizId(), "1");
            }
            if ("testBizIdWithRawString".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getBizId(), "2");
            }

            if ("testReturnStr".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getReturnStr(), "\"returnStr\"");
            }

            if ("testRecordResult".equals(logDTO.getBizType())) {
                Assertions.assertNull(logDTO.getReturnStr());
            }

            if ("testReturnObject".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getReturnStr(), "{\"id\":1,\"name\":\"张三\"}");
            }

            if ("testException".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getException(), "testException");
            }

            if ("testMsgAndExtraWithSpEL".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getMsg(), "将旧值张三更改为新值李四");
                Assertions.assertEquals(logDTO.getExtra(), "将旧值张三更改为新值李四");
            }
            if ("testMsgAndExtraWithRawString".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getMsg(), "str");
                Assertions.assertEquals(logDTO.getExtra(), "str");
            }
            if ("testMsgAndExtraWithObject".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getMsg(), "{\"id\":1,\"name\":\"name\"}");
                Assertions.assertEquals(logDTO.getExtra(), "{\"id\":1,\"name\":\"name\"}");
            }

            if ("testMethodWithCustomName".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getBizId(), "testMethodWithCustomName");
            }
            if ("testMethodWithoutCustomName".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getBizId(), "testMethodWithoutCustomName");
            }

            if ("testOperatorIdWithSpEL".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getOperatorId(), "001");
            }
            if ("testOperatorIdWithCustomOperatorIdGetService".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getOperatorId(), "操作人");
            }

            if ("testExecuteBeforeFunc1".equals(logDTO.getBizType())) {
                Assertions.assertNull(logDTO.getBizId());
            }
            if ("testExecuteAfterFunc".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getBizId(), "valueInBiz");
            }
            if ("testExecuteBeforeFunc2".equals(logDTO.getBizType())) {
                Assertions.assertNull(logDTO.getBizId());
            }
        }
    }
}
