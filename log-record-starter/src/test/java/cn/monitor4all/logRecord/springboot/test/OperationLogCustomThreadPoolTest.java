package cn.monitor4all.logRecord.springboot.test;


import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.springboot.LogRecordAutoConfiguration;
import cn.monitor4all.logRecord.springboot.test.service.OperatorIdGetService;
import cn.monitor4all.logRecord.springboot.test.service.TestService;
import cn.monitor4all.logRecord.springboot.test.utils.TestHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

/**
 * 单元测试：自定义线程池
 */
@Slf4j
@SpringBootTest
@ContextConfiguration(classes = {
        LogRecordAutoConfiguration.class,
        OperatorIdGetService.class,
        TestService.class,})
@PropertySource("classpath:testCustomThreadPool.properties")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class OperationLogCustomThreadPoolTest {

    @Autowired
    private TestService testService;

    /**
     * 测试：用户传入自定义线程池
     */
    @Test
    public void testCustomThreadPool() {
        TestHelper.addLock("testCustomThreadPool");
        testService.testCustomThreadPool();
        TestHelper.await("testCustomThreadPool");
        LogDTO logDTO = TestHelper.getLogDTO("testCustomThreadPool");

        Assertions.assertEquals(logDTO.getBizType(), "testCustomThreadPool");
    }

}
