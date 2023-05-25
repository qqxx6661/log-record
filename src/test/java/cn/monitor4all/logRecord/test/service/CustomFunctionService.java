package cn.monitor4all.logRecord.test.service;

import cn.monitor4all.logRecord.annotation.LogRecordFunc;
import cn.monitor4all.logRecord.test.bean.TestUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;


@Service
@Slf4j
@LogRecordFunc("CustomFunctionService")
public class CustomFunctionService {

    @LogRecordFunc
    public String testMethodWithNoParam() {
        return "testMethodWithNoParam";
    }

    /**
     * 这里SPEL不支持方法重载
     * 参数和返回为相同对象则_DIFF无法比较出差异
     */
    @LogRecordFunc(value = "testMethodWithParam")
    public TestUser testMethodWithParam(TestUser testUser) {
        return testUser;
    }


    @LogRecordFunc(value = "testMethodWithNoReturn")
    public void testMethodWithNoReturn(TestUser testUser) {
    }
}
