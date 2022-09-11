package cn.monitor4all.logRecord.test;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.configuration.LogRecordAutoConfiguration;
import cn.monitor4all.logRecord.service.IOperationLogGetService;
import cn.monitor4all.logRecord.test.bean.TestUser;
import cn.monitor4all.logRecord.test.service.OperationLogGetService;
import cn.monitor4all.logRecord.test.service.TestService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
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
@PropertySource("classpath:test.properties")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class LogOperationTest {

    @Autowired
    private TestService testService;

    @Test
    public void logRecordFuncTest() {
        testService.testBizId("1");
        testService.testTag("tag1");
        testService.testRecordReturnValue();
        testService.testReturnObject();
        try {
            testService.testException();
        } catch (Exception ignored) {}
        testService.testMsgAndExtra("李四", new TestUser(1, "name"));
        testService.testCustomFunc();
        testService.testOperatorId("001");
        testService.testExecuteBeforeFunc();
        testService.testObjectDiff(new TestUser(2, "李四"));
        testService.testCondition(new TestUser(1, "张三"));
        testService.testCustomSuccess(new TestUser(1, "张三"));
        testService.testDefaultParamReturn();
        try {
            testService.testDefaultParamErrorMsg();
        } catch (Exception ignored) {}
        testService.testEnumAndConstantWithSpEL();
        testService.testLogRecordContext();
        testService.testMapUseInLogRecordContext();
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

            if ("testTagWithSpEL".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getTag(), "tag1");
            }
            if ("testTagWithRawString".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getTag(), "tag2");
            }

            if ("testRecordReturnValueTrue".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getReturnStr(), "\"returnStr\"");
            }

            if ("testRecordReturnValueFalse".equals(logDTO.getBizType())) {
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

            if ("testObjectDiff".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getMsg(), "【用户工号】从【1】变成了【2】 【name】从【张三】变成了【李四】");
                Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassName(), "cn.monitor4all.logRecord.test.bean.TestUser");
                Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassAlias(), "用户信息实体");
                Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getFieldName(), "id");
                Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldFieldAlias(), "用户工号");
                Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewFieldAlias(), "用户工号");
                Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldValue(), 1);
                Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewValue(), 2);
            }

            if ("testCondition1".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getBizId(), "1");
            }
            if ("testCondition2".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getBizId(), "2");
            }

            if ("testCustomSuccess1".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getSuccess(), true);
            }
            if ("testCustomSuccess2".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getSuccess(), false);
            }
            if ("testCustomSuccess3".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getSuccess(), true);
            }

            if ("testDefaultParamReturn".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getSuccess(), true);
                Assertions.assertEquals(logDTO.getMsg(), "{\"id\":1,\"name\":\"张三\"}");
            }
            if ("testDefaultParamErrorMsg".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getSuccess(), false);
                Assertions.assertEquals(logDTO.getMsg(), "exception");
            }

            if ("testConstantWithSpEL".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getTag(), "type1");
            }
            if ("testEnumWithSpEL1".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getTag(), "TYPE1");
            }
            if ("testEnumWithSpEL2".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getTag(), "type1");
            }
            if ("testEnumWithSpEL3".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getTag(), "枚举1");
            }

            if ("testLogRecordContext".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getMsg(), "customValue");
            }

            if ("testMapUseInLogRecordContext".equals(logDTO.getBizType())) {
                Assertions.assertEquals(logDTO.getMsg(), "{\"customKey\":\"customValue\"}");
            }

        }
    }
}
