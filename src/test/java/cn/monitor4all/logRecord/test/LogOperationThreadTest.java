package cn.monitor4all.logRecord.test;

import cn.monitor4all.logRecord.aop.SystemLogAspect;
import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.configuration.LogRecordAutoConfiguration;
import cn.monitor4all.logRecord.test.service.TestService;
import cn.monitor4all.logRecord.thread.LogRecordThreadWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * 单元测试：线程相关
 */
@Slf4j
@SpringBootTest
@ContextConfiguration(classes = {
        LogRecordAutoConfiguration.class,
        TestService.class,})
@PropertySource("classpath:testNormal.properties")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class LogOperationThreadTest {

    @Autowired
    private TestService testService;

    @Autowired
    private SystemLogAspect systemLogAspect;

    @Test
    public void logRecordFuncTest() throws NoSuchFieldException, IllegalAccessException {
        // FIXME 这里用注入的方式修改LogRecordThreadWrapper，目的是防止用Bean注入的方式其他测试类也被新的Wrapper包装，不是很优雅，需要重构
        Field field = SystemLogAspect.class.getDeclaredField("logRecordThreadWrapper");
        field.setAccessible(true);
        field.set(systemLogAspect, new LogRecordThreadWrapper() {
            @Override
            public Runnable createLog(Consumer<LogDTO> consumer, LogDTO logDTO) {
                log.info("Before send createLog task to LogRecordThreadPool. Current thread [{}]", Thread.currentThread().getName());
                logDTO.setExtra("extraInfo");
                return LogRecordThreadWrapper.super.createLog(consumer, logDTO);
            }
        });
        // FIXME 主线程结束太快可能会导致线程池打印logDTO消失，需要进行调试
        testService.testLogRecordThreadWrapper();
    }
}
