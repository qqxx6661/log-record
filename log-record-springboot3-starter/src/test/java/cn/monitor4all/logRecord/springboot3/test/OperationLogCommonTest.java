package cn.monitor4all.logRecord.springboot3.test;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.springboot3.LogRecordAutoConfiguration;
import cn.monitor4all.logRecord.springboot3.test.bean.TestComplexUser;
import cn.monitor4all.logRecord.springboot3.test.bean.TestUser;
import cn.monitor4all.logRecord.springboot3.test.bean.diff.extend.TestDiffChildClass;
import cn.monitor4all.logRecord.springboot3.test.bean.diff.nested.TestDiffDuty;
import cn.monitor4all.logRecord.springboot3.test.bean.diff.nested.TestDiffJob;
import cn.monitor4all.logRecord.springboot3.test.bean.diff.nested.TestDiffUserParam;
import cn.monitor4all.logRecord.springboot3.test.service.OperatorIdGetService;
import cn.monitor4all.logRecord.springboot3.test.service.TestService;
import cn.monitor4all.logRecord.springboot3.test.utils.TestHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;

/**
 * 单元测试
 */
@Slf4j
@SpringBootTest
@ContextConfiguration(classes = {
        LogRecordAutoConfiguration.class,
        OperatorIdGetService.class,
        TestService.class,})
@PropertySource("classpath:testCommon.properties")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class OperationLogCommonTest {

    @Autowired
    private TestService testService;

    @Test
    public void testBizIdWithSpEL() {
        TestHelper.addLock("testBizIdWithSpEL");
        testService.testBizIdWithSpEL("1");
        TestHelper.await("testBizIdWithSpEL");
        LogDTO logDTO = TestHelper.getLogDTO("testBizIdWithSpEL");

        Assertions.assertEquals(logDTO.getBizId(), "1");
    }

    @Test
    public void testBizIdWithRawString() {
        TestHelper.addLock("testBizIdWithRawString");
        testService.testBizIdWithRawString();
        TestHelper.await("testBizIdWithRawString");
        LogDTO logDTO = TestHelper.getLogDTO("testBizIdWithRawString");

        Assertions.assertEquals(logDTO.getBizId(), "2");
    }

    @Test
    public void testTagWithSpEL() {
        TestHelper.addLock("testTagWithSpEL");
        testService.testTagWithSpEL("tag1");
        TestHelper.await("testTagWithSpEL");
        LogDTO logDTO = TestHelper.getLogDTO("testTagWithSpEL");

        Assertions.assertEquals(logDTO.getTag(), "tag1");
    }

    @Test
    public void testTagWithRawString() {
        TestHelper.addLock("testTagWithRawString");
        testService.testTagWithRawString();
        TestHelper.await("testTagWithRawString");
        LogDTO logDTO = TestHelper.getLogDTO("testTagWithRawString");

        Assertions.assertEquals(logDTO.getTag(), "tag2");
    }

    @Test
    public void testMsgWithSpEL() {
        TestHelper.addLock("testMsgWithSpEL");
        testService.testMsgWithSpEL("李四", new TestUser(1, "name"));
        TestHelper.await("testMsgWithSpEL");
        LogDTO logDTO = TestHelper.getLogDTO("testMsgWithSpEL");

        Assertions.assertEquals(logDTO.getMsg(), "将旧值张三更改为新值李四");
    }

    @Test
    public void testMsgWithSpELNull() {
        TestHelper.addLock("testMsgWithSpELNull");
        testService.testMsgWithSpELNull();
        TestHelper.await("testMsgWithSpELNull");
        LogDTO logDTO = TestHelper.getLogDTO("testMsgWithSpELNull");

        Assertions.assertNull(logDTO.getMsg());
    }

    @Test
    public void testMsgWithRawString() {
        TestHelper.addLock("testMsgWithRawString");
        testService.testMsgWithRawString();
        TestHelper.await("testMsgWithRawString");
        LogDTO logDTO = TestHelper.getLogDTO("testMsgWithRawString");

        Assertions.assertEquals(logDTO.getMsg(), "str");
    }

    @Test
    public void testMsgWithObject() {
        TestHelper.addLock("testMsgWithObject");
        testService.testMsgWithObject(new TestUser(1, "张三"));
        TestHelper.await("testMsgWithObject");
        LogDTO logDTO = TestHelper.getLogDTO("testMsgWithObject");

        Assertions.assertEquals(logDTO.getMsg(), "{\"id\":1,\"name\":\"张三\"}");
    }

    @Test
    public void testExtraWithSpEL() {
        TestHelper.addLock("testExtraWithSpEL");
        testService.testExtraWithSpEL("李四", new TestUser(1, "name"));
        TestHelper.await("testExtraWithSpEL");
        LogDTO logDTO = TestHelper.getLogDTO("testExtraWithSpEL");

        Assertions.assertEquals(logDTO.getExtra(), "将旧值张三更改为新值李四");
    }

    @Test
    public void testExtraWithSpELNull() {
        TestHelper.addLock("testExtraWithSpELNull");
        testService.testExtraWithSpELNull();
        TestHelper.await("testExtraWithSpELNull");
        LogDTO logDTO = TestHelper.getLogDTO("testExtraWithSpELNull");

        Assertions.assertNull(logDTO.getExtra());
    }

    @Test
    public void testExtraWithRawString() {
        TestHelper.addLock("testExtraWithRawString");
        testService.testExtraWithRawString();
        TestHelper.await("testExtraWithRawString");
        LogDTO logDTO = TestHelper.getLogDTO("testExtraWithRawString");

        Assertions.assertEquals(logDTO.getExtra(), "str");
    }

    @Test
    public void testExtraWithObject() {
        TestHelper.addLock("testExtraWithObject");
        testService.testExtraWithObject(new TestUser(1, "张三"));
        TestHelper.await("testExtraWithObject");
        LogDTO logDTO = TestHelper.getLogDTO("testExtraWithObject");

        Assertions.assertEquals(logDTO.getExtra(), "{\"id\":1,\"name\":\"张三\"}");
    }

    @Test
    public void testRecordReturnValueTrue() {
        TestHelper.addLock("testRecordReturnValueTrue");
        testService.testRecordReturnValueTrue();
        TestHelper.await("testRecordReturnValueTrue");
        LogDTO logDTO = TestHelper.getLogDTO("testRecordReturnValueTrue");

        Assertions.assertEquals(logDTO.getReturnStr(), "\"returnStr\"");
    }

    @Test
    public void testRecordReturnValueFalse() {
        TestHelper.addLock("testRecordReturnValueFalse");
        testService.testRecordReturnValueFalse();
        TestHelper.await("testRecordReturnValueFalse");
        LogDTO logDTO = TestHelper.getLogDTO("testRecordReturnValueFalse");

        Assertions.assertNull(logDTO.getReturnStr());
    }

    @Test
    public void testReturnObjectToJson() {
        TestHelper.addLock("testReturnObjectToJson");
        testService.testReturnObjectToJson();
        TestHelper.await("testReturnObjectToJson");
        LogDTO logDTO = TestHelper.getLogDTO("testReturnObjectToJson");

        Assertions.assertEquals(logDTO.getReturnStr(), "{\"id\":1,\"name\":\"张三\"}");
    }

    @Test
    public void testMethodThrowException() {
        TestHelper.addLock("testMethodThrowException");
        try {
            testService.testMethodThrowException();
        } catch (Exception ignored) {}
        TestHelper.await("testMethodThrowException");
        LogDTO logDTO = TestHelper.getLogDTO("testMethodThrowException");

        Assertions.assertEquals(logDTO.getException(), "testMethodThrowException");
    }

    @Test
    public void testStaticMethodWithCustomName() {
        TestHelper.addLock("testStaticMethodWithCustomName");
        testService.testStaticMethodWithCustomName();
        TestHelper.await("testStaticMethodWithCustomName");
        LogDTO logDTO = TestHelper.getLogDTO("testStaticMethodWithCustomName");

        Assertions.assertEquals(logDTO.getBizId(), "testStaticMethodWithCustomName");
    }

    @Test
    public void testStaticMethodWithoutCustomName() {
        TestHelper.addLock("testStaticMethodWithoutCustomName");
        testService.testStaticMethodWithoutCustomName();
        TestHelper.await("testStaticMethodWithoutCustomName");
        LogDTO logDTO = TestHelper.getLogDTO("testStaticMethodWithoutCustomName");

        Assertions.assertEquals(logDTO.getBizId(), "testStaticMethodWithoutCustomName");
    }

    @Test
    public void testOperatorIdWithSpEL() {
        TestHelper.addLock("testOperatorIdWithSpEL");
        testService.testOperatorIdWithSpEL("001");
        TestHelper.await("testOperatorIdWithSpEL");
        LogDTO logDTO = TestHelper.getLogDTO("testOperatorIdWithSpEL");

        Assertions.assertEquals(logDTO.getOperatorId(), "001");
    }

    @Test
    public void testOperatorIdWithCustomOperatorIdGetService() {
        TestHelper.addLock("testOperatorIdWithCustomOperatorIdGetService");
        testService.testOperatorIdWithCustomOperatorIdGetService();
        TestHelper.await("testOperatorIdWithCustomOperatorIdGetService");
        LogDTO logDTO = TestHelper.getLogDTO("testOperatorIdWithCustomOperatorIdGetService");

        Assertions.assertEquals(logDTO.getOperatorId(), "操作人");
    }

    @Test
    public void testExecuteBeforeFunc() {
        TestHelper.addLock("testExecuteBeforeFunc");
        testService.testExecuteBeforeFunc();
        TestHelper.await("testExecuteBeforeFunc");
        LogDTO logDTO = TestHelper.getLogDTO("testExecuteBeforeFunc");

        Assertions.assertNull(logDTO.getTag());
    }

    @Test
    public void testExecuteAfterFunc() {
        TestHelper.addLock("testExecuteAfterFunc");
        testService.testExecuteAfterFunc();
        TestHelper.await("testExecuteAfterFunc");
        LogDTO logDTO = TestHelper.getLogDTO("testExecuteAfterFunc");

        Assertions.assertEquals(logDTO.getTag(), "value");
    }

    @Test
    public void testLogRecordDiffField() {
        TestHelper.addLock("testLogRecordDiffField");
        testService.testLogRecordDiffField(new TestUser(2, "李四"));
        TestHelper.await("testLogRecordDiffField");
        LogDTO logDTO = TestHelper.getLogDTO("testLogRecordDiffField");

        Assertions.assertEquals(logDTO.getMsg(), "【用户工号】从【1】变成了【2】 【name】从【张三】变成了【李四】");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassName(), "cn.monitor4all.logRecord.springboot3.test.bean.TestUser");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassAlias(), "用户信息实体");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getFieldName(), "id");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldFieldAlias(), "用户工号");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewFieldAlias(), "用户工号");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldValue(), 1);
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewValue(), 2);
    }

    @Test
    public void testLogRecordDiffObject() {
        TestHelper.addLock("testLogRecordDiffObject");
        testService.testLogRecordDiffObject(new TestComplexUser(2, null, 20, Arrays.asList("小李四", "大李四"), "后端"));
        TestHelper.await("testLogRecordDiffObject");
        LogDTO logDTO = TestHelper.getLogDTO("testLogRecordDiffObject");

        Assertions.assertEquals(logDTO.getMsg(), "【用户工号】从【1】变成了【2】 【name】从【张三】变成了【 】 【age】从【 】变成了【20】 【nickNameList】从【[小张三, 大张三]】变成了【[小李四, 大李四]】");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassName(), "cn.monitor4all.logRecord.springboot3.test.bean.TestComplexUser");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassAlias(), "用户信息复杂实体");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getFieldName(), "id");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldFieldAlias(), "用户工号");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewFieldAlias(), "用户工号");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldValue(), 1);
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewValue(), 2);
    }

    @Test
    public void testLogRecordDiffIgnoreField() {
        TestHelper.addLock("testLogRecordDiffIgnoreField");
        testService.testLogRecordDiffIgnoreField(new TestComplexUser(2, null, 20, Arrays.asList("小李四", "大李四"), "后端"));
        TestHelper.await("testLogRecordDiffIgnoreField");
        LogDTO logDTO = TestHelper.getLogDTO("testLogRecordDiffIgnoreField");

        Assertions.assertEquals(logDTO.getMsg(), "【用户工号】从【1】变成了【2】 【name】从【张三】变成了【 】 【age】从【 】变成了【20】 【nickNameList】从【[小张三, 大张三]】变成了【[小李四, 大李四]】");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassName(), "cn.monitor4all.logRecord.springboot3.test.bean.TestComplexUser");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassAlias(), "用户信息复杂实体");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getFieldName(), "id");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldFieldAlias(), "用户工号");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewFieldAlias(), "用户工号");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldValue(), 1);
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewValue(), 2);
    }

    @Test
    public void testLogRecordDiffNestedClass() {
        TestHelper.addLock("testLogRecordDiffNestedClass");
        testService.testLogRecordDiffNestedClass(generateTestDiffUserParam());
        TestHelper.await("testLogRecordDiffNestedClass");
        LogDTO logDTO = TestHelper.getLogDTO("testLogRecordDiffNestedClass");

        Assertions.assertEquals(logDTO.getMsg(), "【id】从【2】变成了【3】 【name】从【小张三】变成了【小李四】" +
                " 【jobList】从【[TestDiffJob(jobId=22, jobName=222, dutyList=[TestDiffDuty(dutyId=222, dutyName=222)])]】" +
                "变成了【[TestDiffJob(jobId=22, jobName=222, dutyList=[TestDiffDuty(dutyId=333, dutyName=222)])]】");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassName(), "cn.monitor4all.logRecord.springboot3.test.bean.diff.nested.TestDiffUserVO");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassAlias(), "用户信息嵌套展示实体");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getNewClassName(), "cn.monitor4all.logRecord.springboot3.test.bean.diff.nested.TestDiffUserParam");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getNewClassAlias(), "用户信息嵌套入参实体");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getFieldName(), "id");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldValue(), 2);
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewValue(), 3);
    }

    @Test
    public void testMultipleDiff() {
        TestHelper.addLock("testMultipleDiff");
        testService.testMultipleDiff(new TestUser(2, "李四"));
        TestHelper.await("testMultipleDiff");
        LogDTO logDTO = TestHelper.getLogDTO("testMultipleDiff");

        Assertions.assertEquals(logDTO.getMsg(), "第一个DIFF：【用户工号】从【1】变成了【2】 【name】从【张三】变成了【李四】第二个DIFF【用户工号】从【3】变成了【2】 【name】从【王五】变成了【李四】");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassName(), "cn.monitor4all.logRecord.springboot3.test.bean.TestUser");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassAlias(), "用户信息实体");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getFieldName(), "id");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldFieldAlias(), "用户工号");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewFieldAlias(), "用户工号");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldValue(), 1);
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewValue(), 2);
    }

    @Test
    public void testExtendClassDiff() {
        TestHelper.addLock("testExtendClassDiff");
        TestDiffChildClass testDiffChildClass = new TestDiffChildClass();
        testDiffChildClass.setParamFromParent("newObject parentParam");
        testDiffChildClass.setParamFromChild("newObject childParam");
        testService.testExtendClassDiff(testDiffChildClass);
        TestHelper.await("testExtendClassDiff");
        LogDTO logDTO = TestHelper.getLogDTO("testExtendClassDiff");

        Assertions.assertEquals(logDTO.getMsg(), "【paramFromChild】从【oldObject childParam】变成了【newObject childParam】 【paramFromParent】从【oldObject parentParam】变成了【newObject parentParam】");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassName(), "cn.monitor4all.logRecord.springboot3.test.bean.diff.extend.TestDiffChildClass");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassAlias(), "DIFF测试类子类");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getFieldName(), "paramFromChild");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldValue(), "oldObject childParam");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewValue(), "newObject childParam");
    }

    @Test
    public void testConditionTrue() {
        TestHelper.addLock("testConditionTrue");
        testService.testConditionTrue(new TestUser(1, "张三"));
        TestHelper.await("testConditionTrue");
        LogDTO logDTO = TestHelper.getLogDTO("testConditionTrue");

        Assertions.assertEquals(logDTO.getBizId(), "1");
    }

    /**
     * 测试条件不满足时，不记录日志
     * 不会生成logDTO
     */
    @Test
    public void testConditionFalse() {
        TestHelper.addLock("testConditionFalse");
        testService.testConditionFalse(new TestUser(2, "张三"));
        TestHelper.await("testConditionFalse");
        LogDTO logDTO = TestHelper.getLogDTO("testConditionFalse");

        Assertions.assertNull(logDTO);
    }

    @Test
    public void testCustomSuccessTrue() {
        TestHelper.addLock("testCustomSuccessTrue");
        testService.testCustomSuccessTrue(new TestUser(1, "张三"));
        TestHelper.await("testCustomSuccessTrue");
        LogDTO logDTO = TestHelper.getLogDTO("testCustomSuccessTrue");

        Assertions.assertEquals(logDTO.getSuccess(), true);
    }

    @Test
    public void testCustomSuccessFalse() {
        TestHelper.addLock("testCustomSuccessFalse");
        testService.testCustomSuccessFalse(new TestUser(1, "张三"));
        TestHelper.await("testCustomSuccessFalse");
        LogDTO logDTO = TestHelper.getLogDTO("testCustomSuccessFalse");

        Assertions.assertEquals(logDTO.getSuccess(), false);
    }

    @Test
    public void testDefaultParamReturn() {
        TestHelper.addLock("testDefaultParamReturn");
        testService.testDefaultParamReturn();
        TestHelper.await("testDefaultParamReturn");
        LogDTO logDTO = TestHelper.getLogDTO("testDefaultParamReturn");

        Assertions.assertEquals(logDTO.getSuccess(), true);
        Assertions.assertEquals(logDTO.getMsg(), "{\"id\":1,\"name\":\"张三\"}");
    }

    @Test
    public void testDefaultParamErrorMsg() {
        TestHelper.addLock("testDefaultParamErrorMsg");
        try {
            testService.testDefaultParamErrorMsg();
        } catch (Exception ignored) {}
        TestHelper.await("testDefaultParamErrorMsg");
        LogDTO logDTO = TestHelper.getLogDTO("testDefaultParamErrorMsg");

        Assertions.assertEquals(logDTO.getSuccess(), false);
        Assertions.assertEquals(logDTO.getMsg(), "testDefaultParamErrorMsg");
    }

    @Test
    public void testConstantWithSpEL() {
        TestHelper.addLock("testConstantWithSpEL");
        testService.testConstantWithSpEL();
        TestHelper.await("testConstantWithSpEL");
        LogDTO logDTO = TestHelper.getLogDTO("testConstantWithSpEL");

        Assertions.assertEquals(logDTO.getTag(), "type1");
    }

    @Test
    public void testEnumWithSpEL1() {
        TestHelper.addLock("testEnumWithSpEL1");
        testService.testEnumWithSpEL1();
        TestHelper.await("testEnumWithSpEL1");
        LogDTO logDTO = TestHelper.getLogDTO("testEnumWithSpEL1");

        Assertions.assertEquals(logDTO.getTag(), "TYPE1");
    }

    @Test
    public void testEnumWithSpEL2() {
        TestHelper.addLock("testEnumWithSpEL2");
        testService.testEnumWithSpEL2();
        TestHelper.await("testEnumWithSpEL2");
        LogDTO logDTO = TestHelper.getLogDTO("testEnumWithSpEL2");

        Assertions.assertEquals(logDTO.getTag(), "type1");
    }

    @Test
    public void testEnumWithSpEL3() {
        TestHelper.addLock("testEnumWithSpEL3");
        testService.testEnumWithSpEL3();
        TestHelper.await("testEnumWithSpEL3");
        LogDTO logDTO = TestHelper.getLogDTO("testEnumWithSpEL3");

        Assertions.assertEquals(logDTO.getTag(), "枚举1");
    }

    @Test
    public void testSpELInLogRecordContext() {
        TestHelper.addLock("testSpELInLogRecordContext");
        testService.testSpELInLogRecordContext();
        TestHelper.await("testSpELInLogRecordContext");
        LogDTO logDTO = TestHelper.getLogDTO("testSpELInLogRecordContext");

        Assertions.assertEquals(logDTO.getMsg(), "customValue");
    }

    @Test
    public void testLogRecordContextTransmittableThreadLocal() {
        TestHelper.addLock("testLogRecordContextTransmittableThreadLocal");
        testService.testLogRecordContextTransmittableThreadLocal();
        TestHelper.await("testLogRecordContextTransmittableThreadLocal");
        LogDTO logDTO = TestHelper.getLogDTO("testLogRecordContextTransmittableThreadLocal");

        Assertions.assertEquals(logDTO.getMsg(), "customValue");
    }

    @Test
    public void testMapUseInLogRecordContext() {
        TestHelper.addLock("testMapUseInLogRecordContext");
        testService.testMapUseInLogRecordContext();
        TestHelper.await("testMapUseInLogRecordContext");
        LogDTO logDTO = TestHelper.getLogDTO("testMapUseInLogRecordContext");

        Assertions.assertEquals(logDTO.getMsg(), "{\"customKey\":\"customValue\"}");
    }

    private TestDiffUserParam generateTestDiffUserParam() {
        TestDiffUserParam testDiffUserParam = new TestDiffUserParam();
        testDiffUserParam.setId(3);
        testDiffUserParam.setName("小李四");
        testDiffUserParam.setAge(2);
        TestDiffJob testDiffJob = new TestDiffJob();
        testDiffJob.setJobId(22);
        testDiffJob.setJobName("222");
        TestDiffDuty testDiffDuty = new TestDiffDuty();
        testDiffDuty.setDutyId(333);
        testDiffDuty.setDutyName("222");
        testDiffJob.setDutyList(Arrays.asList(testDiffDuty));
        testDiffUserParam.setJobList(Arrays.asList(testDiffJob));
        return testDiffUserParam;
    }
}
