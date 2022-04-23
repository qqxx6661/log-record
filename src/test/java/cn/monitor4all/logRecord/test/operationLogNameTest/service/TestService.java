package cn.monitor4all.logRecord.test.operationLogNameTest.service;


import cn.monitor4all.logRecord.annotation.OperationLog;
import cn.monitor4all.logRecord.context.LogRecordContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.test.context.TestComponent;


@TestComponent
public class TestService {

    @OperationLog(bizId = "#bizId", bizType = "testBizIdWithSpEL")
    @OperationLog(bizId = "2", bizType = "testBizIdWithRawString")
    public void testBizId(String bizId) {
    }

    @OperationLog(bizId = "1", bizType = "testReturnStr")
    public String testReturnStr() {
        return "returnStr";
    }

    @OperationLog(bizId = "1", bizType = "testReturnObject")
    public TestClass testReturnObject() {
        return new TestClass(1,"张三");
    }

    @OperationLog(bizId = "1", bizType = "testException")
    public String testException() {
        throw new RuntimeException("testException");
    }

    @OperationLog(bizId = "1", bizType = "testMsgAndExtraWithSpEL", msg = "'将旧值' + #oldValue + '更改为新值' + #newValue", extra = "'将旧值' + #oldValue + '更改为新值' + #newValue")
    @OperationLog(bizId = "2", bizType = "testMsgAndExtraWithRawString", msg = "'str'", extra = "'str'")
    @OperationLog(bizId = "3", bizType = "testMsgAndExtraWithObject", msg = "#testClass", extra = "#testClass")
    public void testMsgAndExtra(String newValue, TestClass testClass) {
        LogRecordContext.putVariables("oldValue", "张三");
    }

    @OperationLog(bizId = "#test_testMethodWithCustomName()", bizType = "testMethodWithCustomName")
    @OperationLog(bizId = "#test_testMethodWithoutCustomName()", bizType = "testMethodWithoutCustomName")
    public void testCustomFunc() {
    }


    @OperationLog(bizId = "1", bizType = "testOperatorIdWithSpEL", operatorId = "#operatorId")
    @OperationLog(bizId = "2", bizType = "testOperatorIdWithCustomOperatorIdGetService")
    public void testOperatorId(String operatorId) {
    }

    @OperationLog(bizId = "#keyInBiz", bizType = "testExecuteBeforeFunc1", executeBeforeFunc = true)
    @OperationLog(bizId = "#keyInBiz", bizType = "testExecuteAfterFunc")
    @OperationLog(bizId = "#keyInBiz", bizType = "testExecuteBeforeFunc2", executeBeforeFunc = true)
    public void testExecuteBeforeFunc() {
        LogRecordContext.putVariables("keyInBiz", "valueInBiz");
    }


    @Data
    @AllArgsConstructor
    public static class TestClass {
        private Integer id;
        private String name;
    }
}
