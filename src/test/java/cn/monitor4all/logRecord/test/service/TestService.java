package cn.monitor4all.logRecord.test.service;


import cn.monitor4all.logRecord.annotation.OperationLog;
import cn.monitor4all.logRecord.context.LogRecordContext;
import cn.monitor4all.logRecord.test.bean.TestUser;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.TestComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试服务
 */
@TestComponent
public class TestService {

    /**
     * 测试bizId SpEL解析
     */
    @OperationLog(bizId = "#bizId", bizType = "'testBizIdWithSpEL'")
    @OperationLog(bizId = "'2'", bizType = "'testBizIdWithRawString'")
    public void testBizId(String bizId) {
    }

    /**
     * 测试tag SpEL解析
     */
    @OperationLog(bizId = "'1'", tag = "#tag", bizType = "'testTagWithSpEL'")
    @OperationLog(bizId = "'2'", tag = "'tag2'", bizType = "'testTagWithRawString'")
    public void testTag(String tag) {
    }

    /**
     * 测试返回值开关
     */
    @OperationLog(bizId = "'1'", bizType = "'testRecordReturnValueTrue'", recordReturnValue = true)
    @OperationLog(bizId = "'1'", bizType = "'testRecordReturnValueFalse'")
    public String testRecordReturnValue() {
        return "returnStr";
    }

    /**
     * 测试返回值JSON化
     */
    @OperationLog(bizId = "'1'", bizType = "'testReturnObject'", recordReturnValue = true)
    public TestUser testReturnObject() {
        return new TestUser(1, "张三");
    }

    /**
     * 测试exception 返回异常信息
     */
    @OperationLog(bizId = "'1'", bizType = "'testException'")
    public String testException() {
        throw new RuntimeException("testException");
    }

    /**
     * 测试Msg和Extra参数 SpEL解析
     */
    @OperationLog(bizId = "'1'", bizType = "'testMsgAndExtraWithSpEL'", msg = "'将旧值' + #oldValue + '更改为新值' + #newValue", extra = "'将旧值' + #oldValue + '更改为新值' + #newValue")
    @OperationLog(bizId = "'2'", bizType = "'testMsgAndExtraWithRawString'", msg = "'str'", extra = "'str'")
    @OperationLog(bizId = "'3'", bizType = "'testMsgAndExtraWithObject'", msg = "#testUser", extra = "#testUser")
    public void testMsgAndExtra(String newValue, TestUser testUser) {
        LogRecordContext.putVariable("oldValue", "张三");
    }

    /**
     * 测试自定义函数
     */
    @OperationLog(bizId = "#test_testMethodWithCustomName()", bizType = "'testMethodWithCustomName'")
    @OperationLog(bizId = "#test_testMethodWithoutCustomName()", bizType = "'testMethodWithoutCustomName'")
    public void testCustomFunc() {
    }

    /**
     * 测试operationId SpEL解析或者SPI接口获取
     */
    @OperationLog(bizId = "'1'", bizType = "'testOperatorIdWithSpEL'", operatorId = "#operatorId")
    @OperationLog(bizId = "'2'", bizType = "'testOperatorIdWithCustomOperatorIdGetService'")
    public void testOperatorId(String operatorId) {
    }

    /**
     * 测试切面执行方法前方法后配置
     */
    @OperationLog(bizId = "#keyInBiz", bizType = "'testExecuteBeforeFunc1'", executeBeforeFunc = true)
    @OperationLog(bizId = "#keyInBiz", bizType = "'testExecuteAfterFunc'")
    @OperationLog(bizId = "#keyInBiz", bizType = "'testExecuteBeforeFunc2'", executeBeforeFunc = true)
    public void testExecuteBeforeFunc() {
        LogRecordContext.putVariable("keyInBiz", "valueInBiz");
    }

    /**
     * 测试实体类DIFF
     */
    @OperationLog(bizId = "'1'", bizType = "'testObjectDiff'", msg = "#_DIFF(#oldObject, #testUser)", extra = "#_DIFF(#oldObject, #testUser)")
    public void testObjectDiff(TestUser testUser) {
        LogRecordContext.putVariable("oldObject", new TestUser(1, "张三"));
    }

    /**
     * 测试condition条件注解
     */
    @OperationLog(bizId = "'1'", bizType = "'testCondition1'", condition = "#testUser != null")
    @OperationLog(bizId = "'2'", bizType = "'testCondition2'", condition = "#testUser.id == 1")
    @OperationLog(bizId = "'3'", bizType = "'testCondition3'", condition = "#testUser.id == 2")
    public void testCondition(TestUser testUser) {
    }

    /**
     * 测试自定义success
     */
    @OperationLog(bizId = "'success'", bizType = "'testCustomSuccess1'", success = "#testUser != null")
    @OperationLog(bizId = "'failure'", bizType = "'testCustomSuccess2'", success = "#testUser == null")
    @OperationLog(bizId = "''", bizType = "'testCustomSuccess3'")
    public void testCustomSuccess(TestUser testUser) {
    }

    /**
     * 测试通过SpEL获取默认自定义字段
     */
    @OperationLog(bizId = "'1'", bizType = "'testDefaultParamErrorMsg'", msg = "#_errorMsg")
    public TestUser testDefaultParamErrorMsg() {
        throw new RuntimeException("exception");

    }

    /**
     * 测试通过SpEL获取默认自定义字段
     */
    @OperationLog(bizId = "'1'", bizType = "'testDefaultParamReturn'", msg = "#_return")
    public TestUser testDefaultParamReturn() {
        return new TestUser(1, "张三");
    }

    /**
     * 测试SpEL解析枚举类和常量类
     */
    @OperationLog(bizId = "'1'", bizType = "'testConstantWithSpEL'", tag = "T(cn.monitor4all.logRecord.test.bean.TestConstant).TYPE1")
    @OperationLog(bizId = "'2'", bizType = "'testEnumWithSpEL1'", tag = "T(cn.monitor4all.logRecord.test.bean.TestEnum).TYPE1")
    @OperationLog(bizId = "'3'", bizType = "'testEnumWithSpEL2'", tag = "T(cn.monitor4all.logRecord.test.bean.TestEnum).TYPE1.key")
    @OperationLog(bizId = "'4'", bizType = "'testEnumWithSpEL3'", tag = "T(cn.monitor4all.logRecord.test.bean.TestEnum).TYPE1.name")
    public void testEnumAndConstantWithSpEL() {
    }

    /**
     * 测试自定义上下文写入和读取
     */
    @OperationLog(bizId = "'1'", bizType = "'testLogRecordContext'", msg = "#customKey")
    public void testLogRecordContext() {
        LogRecordContext.putVariable("customKey", "customValue");
        Assertions.assertEquals("customValue", LogRecordContext.getVariable("customKey"));
    }

    /**
     * 测试自定义上下文写入和读取Map
     */
    @OperationLog(bizId = "'1'", bizType = "'testMapUseInLogRecordContext'", msg = "#customMap")
    public void testMapUseInLogRecordContext() {
        Map<String, Object> customMap = new HashMap<>(2);
        customMap.put("customKey", "customValue");
        LogRecordContext.putVariable("customMap", customMap);
        Assertions.assertNotNull(LogRecordContext.getVariable("customMap"));
    }
}
