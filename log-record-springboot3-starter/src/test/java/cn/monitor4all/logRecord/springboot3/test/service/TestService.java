package cn.monitor4all.logRecord.springboot3.test.service;


import cn.monitor4all.logRecord.springboot3.test.bean.diff.extend.TestDiffChildClass;
import cn.monitor4all.logRecord.springboot3.test.bean.diff.nested.TestDiffDuty;
import cn.monitor4all.logRecord.springboot3.test.bean.diff.nested.TestDiffJob;
import cn.monitor4all.logRecord.annotation.OperationLog;
import cn.monitor4all.logRecord.context.LogRecordContext;
import cn.monitor4all.logRecord.springboot3.test.bean.TestComplexUser;
import cn.monitor4all.logRecord.springboot3.test.bean.TestUser;
import cn.monitor4all.logRecord.springboot3.test.bean.diff.nested.TestDiffUserParam;
import cn.monitor4all.logRecord.springboot3.test.bean.diff.nested.TestDiffUserVO;
import org.springframework.boot.test.context.TestComponent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试服务
 */
@TestComponent
public class TestService {

    @OperationLog(bizId = "#p0", bizType = "'testBizIdWithSpEL'")
    public void testBizIdWithSpEL(String bizId) {
    }

    @OperationLog(bizId = "'2'", bizType = "'testBizIdWithRawString'")
    public void testBizIdWithRawString() {
    }

    @OperationLog(bizId = "'1'", tag = "#p0", bizType = "'testTagWithSpEL'")
    public void testTagWithSpEL(String tag) {
    }

    @OperationLog(bizId = "'1'", tag = "'tag2'", bizType = "'testTagWithRawString'")
    public void testTagWithRawString() {
    }

    @OperationLog(bizId = "'1'", bizType = "'testMsgWithSpEL'", msg = "'将旧值' + #oldValue + '更改为新值' + #p0", extra = "'将旧值' + #oldValue + '更改为新值' + #p0")
    public void testMsgWithSpEL(String newValue, TestUser testUser) {
        LogRecordContext.putVariable("oldValue", "张三");
    }

    @OperationLog(bizId = "'1'", msg = "#nullKey", bizType = "'testMsgWithSpELNull'")
    public void testMsgWithSpELNull() {
    }

    @OperationLog(bizId = "'1'", bizType = "'testMsgWithRawString'", msg = "'str'", extra = "'str'")
    public void testMsgWithRawString() {
    }

    @OperationLog(bizId = "'1'", bizType = "'testMsgWithObject'", msg = "#p0", extra = "#p0")
    public void testMsgWithObject(TestUser testUser) {
    }

    @OperationLog(bizId = "'1'", bizType = "'testExtraWithSpEL'", msg = "'将旧值' + #oldValue + '更改为新值' + #p0", extra = "'将旧值' + #oldValue + '更改为新值' + #p0")
    public void testExtraWithSpEL(String newValue, TestUser testUser) {
        LogRecordContext.putVariable("oldValue", "张三");
    }

    @OperationLog(bizId = "'1'", msg = "#nullKey", bizType = "'testExtraWithSpELNull'")
    public void testExtraWithSpELNull() {
    }

    @OperationLog(bizId = "'1'", bizType = "'testExtraWithRawString'", msg = "'str'", extra = "'str'")
    public void testExtraWithRawString() {
    }

    @OperationLog(bizId = "'1'", bizType = "'testExtraWithObject'", msg = "#p0", extra = "#p0")
    public void testExtraWithObject(TestUser testUser) {
    }

    @OperationLog(bizId = "'1'", bizType = "'testRecordReturnValueTrue'", recordReturnValue = true)
    public String testRecordReturnValueTrue() {
        return "returnStr";
    }

    @OperationLog(bizId = "'1'", bizType = "'testRecordReturnValueFalse'")
    public String testRecordReturnValueFalse() {
        return "returnStr";
    }

    @OperationLog(bizId = "'1'", bizType = "'testReturnObjectToJson'", recordReturnValue = true)
    public TestUser testReturnObjectToJson() {
        return new TestUser(1, "张三");
    }

    @OperationLog(bizId = "'1'", bizType = "'testMethodThrowException'")
    public String testMethodThrowException() {
        throw new RuntimeException("testMethodThrowException");
    }

    @OperationLog(bizId = "#CustomFunctionStatic_testStaticMethodWithCustomName()", bizType = "'testStaticMethodWithCustomName'")
    public void testStaticMethodWithCustomName() {
    }

    @OperationLog(bizId = "#CustomFunctionStatic_testStaticMethodWithoutCustomName()", bizType = "'testStaticMethodWithoutCustomName'")
    public void testStaticMethodWithoutCustomName() {
    }

    @OperationLog(bizId = "'1'", bizType = "'testOperatorIdWithSpEL'", operatorId = "#p0")
    public void testOperatorIdWithSpEL(String operatorId) {
    }

    @OperationLog(bizId = "'1'", bizType = "'testOperatorIdWithCustomOperatorIdGetService'")
    public void testOperatorIdWithCustomOperatorIdGetService() {
    }


    @OperationLog(bizId = "'1'", tag = "#key", bizType = "'testExecuteBeforeFunc'", executeBeforeFunc = true)
    public void testExecuteBeforeFunc() {
        LogRecordContext.putVariable("key", "value");
    }

    @OperationLog(bizId = "'1'", tag = "#key", bizType = "'testExecuteAfterFunc'")
    public void testExecuteAfterFunc() {
        LogRecordContext.putVariable("key", "value");
    }

    @OperationLog(bizId = "'1'", bizType = "'testLogRecordDiffField'", msg = "#_DIFF(#oldObject, #p0)")
    public void testLogRecordDiffField(TestUser testUser) {
        LogRecordContext.putVariable("oldObject", new TestUser(1, "张三"));
    }

    @OperationLog(bizId = "'1'", bizType = "'testLogRecordDiffObject'", msg = "#_DIFF(#oldObject, #p0)")
    public void testLogRecordDiffObject(TestComplexUser testComplexUser) {
        LogRecordContext.putVariable("oldObject", new TestComplexUser(1, "张三", null,
                Arrays.asList("小张三", "大张三"), "前端"));
    }

    @OperationLog(bizId = "'1'", bizType = "'testLogRecordDiffIgnoreField'", msg = "#_DIFF(#oldObject, #p0)")
    public void testLogRecordDiffIgnoreField(TestComplexUser testComplexUser) {
        LogRecordContext.putVariable("oldObject", new TestComplexUser(1, "张三", null,
                Arrays.asList("小张三", "大张三"), "前端"));
    }

    @OperationLog(bizId = "'1'", bizType = "'testLogRecordDiffIgnoreNewObjectNullValue'", msg = "#_DIFF(#oldObject, #p0)")
    public void testLogRecordDiffIgnoreNewObjectNullValue(TestComplexUser testComplexUser) {
        LogRecordContext.putVariable("oldObject", new TestComplexUser(1, "张三", null,
                Arrays.asList("小张三", "大张三"), "前端"));
    }

    @OperationLog(bizId = "'1'", bizType = "'testLogRecordDiffNestedClass'", msg = "#_DIFF(#oldObject, #p0)")
    public void testLogRecordDiffNestedClass(TestDiffUserParam testDiffUserParam) {
        TestDiffUserVO testDiffUserVO = new TestDiffUserVO();
        testDiffUserVO.setId(2);
        testDiffUserVO.setName("小张三");
        testDiffUserVO.setAge(2);
        TestDiffJob testDiffJob = new TestDiffJob();
        testDiffJob.setJobId(22);
        testDiffJob.setJobName("222");
        TestDiffDuty testDiffDuty = new TestDiffDuty();
        testDiffDuty.setDutyId(222);
        testDiffDuty.setDutyName("222");
        testDiffJob.setDutyList(Arrays.asList(testDiffDuty));
        testDiffUserVO.setJobList(Arrays.asList(testDiffJob));
        LogRecordContext.putVariable("oldObject", testDiffUserVO);
    }

    @OperationLog(bizId = "'1'", bizType = "'testMultipleDiff'", msg = "'第一个DIFF：' + #_DIFF(#oldObject1, #p0) + '第二个DIFF' + #_DIFF(#oldObject2, #p0)")
    public void testMultipleDiff(TestUser testUser) {
        LogRecordContext.putVariable("oldObject1", new TestUser(1, "张三"));
        LogRecordContext.putVariable("oldObject2", new TestUser(3, "王五"));
    }

    @OperationLog(bizId = "'1'", bizType = "'testExtendClassDiff'", msg = "#_DIFF(#oldObject, #p0)")
    public void testExtendClassDiff(TestDiffChildClass testDiffChildClass) {
        TestDiffChildClass oldObject = new TestDiffChildClass();
        oldObject.setParamFromParent("oldObject parentParam");
        oldObject.setParamFromChild("oldObject childParam");
        LogRecordContext.putVariable("oldObject", oldObject);
    }

    @OperationLog(bizId = "'1'", bizType = "'testConditionTrue'", condition = "#p0 != null")
    public void testConditionTrue(TestUser testUser) {
    }


    @OperationLog(bizId = "'1'", bizType = "'testConditionFalse'", condition = "#p0.id == 1")
    public void testConditionFalse(TestUser testUser) {
    }

    @OperationLog(bizId = "'1'", bizType = "'testCustomSuccessTrue'", success = "#p0 != null")
    public void testCustomSuccessTrue(TestUser testUser) {
    }

    @OperationLog(bizId = "'1'", bizType = "'testCustomSuccessFalse'", success = "#p0 == null")
    public void testCustomSuccessFalse(TestUser testUser) {
    }

    @OperationLog(bizId = "'1'", bizType = "'testDefaultParamReturn'", msg = "#_return")
    public TestUser testDefaultParamReturn() {
        return new TestUser(1, "张三");
    }

    @OperationLog(bizId = "'1'", bizType = "'testDefaultParamErrorMsg'", msg = "#_errorMsg")
    public TestUser testDefaultParamErrorMsg() {
        throw new RuntimeException("testDefaultParamErrorMsg");
    }

    @OperationLog(bizId = "'1'", bizType = "'testConstantWithSpEL'", tag = "T(cn.monitor4all.logRecord.springboot3.test.bean..TestConstant).TYPE1")
    public void testConstantWithSpEL() {
    }

    @OperationLog(bizId = "'1'", bizType = "'testEnumWithSpEL1'", tag = "T(cn.monitor4all.logRecord.springboot3.test.bean..TestEnum).TYPE1")
    public void testEnumWithSpEL1() {
    }

    @OperationLog(bizId = "'1'", bizType = "'testEnumWithSpEL2'", tag = "T(cn.monitor4all.logRecord.springboot3.test.bean..TestEnum).TYPE1.key")
    public void testEnumWithSpEL2() {
    }

    @OperationLog(bizId = "'1'", bizType = "'testEnumWithSpEL3'", tag = "T(cn.monitor4all.logRecord.springboot3.test.bean..TestEnum).TYPE1.name")
    public void testEnumWithSpEL3() {
    }

    @OperationLog(bizId = "'1'", bizType = "'testSpELInLogRecordContext'", msg = "#customKey")
    public void testSpELInLogRecordContext() {
        LogRecordContext.putVariable("customKey", "customValue");
    }

    @OperationLog(bizId = "'1'", bizType = "'testMapUseInLogRecordContext'", msg = "#customMap")
    public void testMapUseInLogRecordContext() {
        Map<String, Object> customMap = new HashMap<>(2);
        customMap.put("customKey", "customValue");
        LogRecordContext.putVariable("customMap", customMap);
    }


    @OperationLog(bizId = "'1'", bizType = "'testLogRecordContextTransmittableThreadLocal'")
    public void testLogRecordContextTransmittableThreadLocal() {
        LogRecordContext.putVariable("customKey", "customValue");
    }

    @OperationLog(bizId = "'1'", bizType = "'testRetryTimesAndOperationLogGetErrorHandler'")
    public void testRetryTimesAndOperationLogGetErrorHandler() {
    }

    @OperationLog(bizId = "'1'", bizType = "'testCustomThreadPool'")
    @OperationLog(bizId = "'2'", bizType = "'testCustomThreadPool'")
    @OperationLog(bizId = "'3'", bizType = "'testCustomThreadPool'")
    @OperationLog(bizId = "'4'", bizType = "'testCustomThreadPool'")
    @OperationLog(bizId = "'5'", bizType = "'testCustomThreadPool'")
    public void testCustomThreadPool() {
    }
}
