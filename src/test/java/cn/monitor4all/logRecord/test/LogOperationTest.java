package cn.monitor4all.logRecord.test;

import cn.monitor4all.logRecord.configuration.LogRecordAutoConfiguration;
import cn.monitor4all.logRecord.test.bean.TestComplexUser;
import cn.monitor4all.logRecord.test.bean.TestUser;
import cn.monitor4all.logRecord.test.bean.diff.TestDiffDuty;
import cn.monitor4all.logRecord.test.bean.diff.TestDiffJob;
import cn.monitor4all.logRecord.test.bean.diff.TestDiffUserParam;
import cn.monitor4all.logRecord.test.service.OperatorIdGetService;
import cn.monitor4all.logRecord.test.service.TestService;
import lombok.extern.slf4j.Slf4j;
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
@PropertySource("classpath:testNormal.properties")
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
        testService.testLogRecordDiffField(new TestUser(2, "李四"));
        testService.testLogRecordDiffObject(new TestComplexUser(2, null, 20,
                Arrays.asList("小李四", "大李四"), "后端"));
        testService.testLogRecordDiffIgnoreField(new TestComplexUser(2, null, 20,
                Arrays.asList("小李四", "大李四"), "后端"));
        testService.testMultipleDiff(new TestUser(2, "李四"));
        testService.testLogRecordDiffNestedClass(generateTestDiffUserParam());
        testService.testCondition(new TestUser(1, "张三"));
        testService.testCustomSuccess(new TestUser(1, "张三"));
        testService.testDefaultParamReturn();
        try {
            testService.testDefaultParamErrorMsg();
        } catch (Exception ignored) {}
        testService.testEnumAndConstantWithSpEL();
        testService.testLogRecordContext();
        testService.testMapUseInLogRecordContext();
        testService.testMsgWithSpELNull();
        testService.testExtraWithSpELNull();
        testService.testSpringBeanCustomFuncNoParam(new TestUser(2, "dsa"));
        testService.testSpringBeanCustomFuncWithParam(new TestUser(2, "dsa"));
        testService.testSpringBeanCustomFuncNoReturn(20);
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
