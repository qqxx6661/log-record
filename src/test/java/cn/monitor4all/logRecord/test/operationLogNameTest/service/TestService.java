package cn.monitor4all.logRecord.test.operationLogNameTest.service;


import cn.monitor4all.logRecord.annotation.OperationLog;
import cn.monitor4all.logRecord.context.LogRecordContext;
import cn.monitor4all.logRecord.test.operationLogNameTest.bean.TestUser;
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

    @OperationLog(bizId = "1", bizType = "testRecordReturnValueTrue")
    @OperationLog(bizId = "1", bizType = "testRecordReturnValueFalse", recordReturnValue = false)
    public String testRecordReturnValue() {
        return "returnStr";
    }

    @OperationLog(bizId = "1", bizType = "testReturnObject")
    public TestUser testReturnObject() {
        return new TestUser(1, "张三");
    }

    @OperationLog(bizId = "1", bizType = "testException")
    public String testException() {
        throw new RuntimeException("testException");
    }

    @OperationLog(bizId = "1", bizType = "testMsgAndExtraWithSpEL", msg = "'将旧值' + #oldValue + '更改为新值' + #newValue", extra = "'将旧值' + #oldValue + '更改为新值' + #newValue")
    @OperationLog(bizId = "2", bizType = "testMsgAndExtraWithRawString", msg = "'str'", extra = "'str'")
    @OperationLog(bizId = "3", bizType = "testMsgAndExtraWithObject", msg = "#testUser", extra = "#testUser")
    public void testMsgAndExtra(String newValue, TestUser testUser) {
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

    @OperationLog(bizId = "1", bizType = "testObjectDiff", msg = "#_DIFF(#oldObject, #testUser)", extra = "#_DIFF(#oldObject, #testUser)")
    public void testObjectDiff(TestUser testUser) {
        LogRecordContext.putVariables("oldObject", new TestUser(1, "张三"));
    }

    @OperationLog(bizId = "1", bizType = "testCondition1", condition = "#testUser != null")
    @OperationLog(bizId = "2", bizType = "testCondition2", condition = "#testUser.id == 1")
    @OperationLog(bizId = "3", bizType = "testCondition3", condition = "#testUser.id == 2")
    public void testCondition(TestUser testUser) {
    }
}
