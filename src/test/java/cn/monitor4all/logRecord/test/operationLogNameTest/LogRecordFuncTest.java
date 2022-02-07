package cn.monitor4all.logRecord.test.operationLogNameTest;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.configuration.LogRecordAutoConfiguration;
import cn.monitor4all.logRecord.function.LogRecordFunc;
import cn.monitor4all.logRecord.service.CustomLogListener;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author pumbf
 * @version 1.0
 * @since 2022-01-28 21:16
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = {LogRecordAutoConfiguration.class, LogRecordFuncTest.TestLogListener.class, OperationLogService.class})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class LogRecordFuncTest {

    @Autowired
    OperationLogService operationLogService;

    @Test
    public void logRecordFuncTest() {
        System.out.println(operationLogService.test());
    }

    @TestComponent
    @Slf4j
    @Scope
    public static class TestLogListener extends CustomLogListener {

        @Override
        public void createLog(LogDTO logDTO) throws Exception {
            log.info(JSON.toJSONString(logDTO));
            Assertions.assertEquals(logDTO.getBizId(),  "test");
        }
    }


    @LogRecordFunc("test")
    public static class FunctionTest {

        @LogRecordFunc("test")
        public static String testMethod(){
            return "test";
        }
    }
}
