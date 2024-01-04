package cn.monitor4all.logRecord.springboot3.test;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.springboot3.LogRecordAutoConfiguration;
import cn.monitor4all.logRecord.springboot3.test.bean.TestComplexUser;
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
@PropertySource("classpath:testLogRecordDiffIgnoreNullValue.properties")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class LogRecordDiffIgnoreNullValueTest {

    @Autowired
    private TestService testService;

    @Test
    public void testLogRecordDiffIgnoreNullValue() {
        TestHelper.addLock("testLogRecordDiffIgnoreNewObjectNullValue");
        testService.testLogRecordDiffIgnoreNewObjectNullValue(new TestComplexUser(2, null, 20, Arrays.asList("小李四", "大李四"), "后端"));
        TestHelper.await("testLogRecordDiffIgnoreNewObjectNullValue");
        LogDTO logDTO = TestHelper.getLogDTO("testLogRecordDiffIgnoreNewObjectNullValue");

        Assertions.assertEquals(logDTO.getMsg(), "【用户工号】从【1】变成了【2】 【nickNameList】从【[小张三, 大张三]】变成了【[小李四, 大李四]】");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassName(), "cn.monitor4all.logRecord.springboot3.test.bean.TestComplexUser");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassAlias(), "用户信息复杂实体");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getFieldName(), "id");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldFieldAlias(), "用户工号");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewFieldAlias(), "用户工号");
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldValue(), 1);
        Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewValue(), 2);
    }
}
