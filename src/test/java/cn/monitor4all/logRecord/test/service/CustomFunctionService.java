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
    public TestUser testUser() {
        return new TestUser(1, "asd");
    }

    /**
     * 这里SPEL不支持方法重载
     * 参数和返回为相同对象则_DIFF无法比较出差异
     * @param testUser
     * @return
     */
    @LogRecordFunc(value = "testUserWithParam")
    public TestUser testUser(TestUser testUser) {
        return new TestUser(3, testUser.getName().toUpperCase(Locale.ROOT));
    }


    @LogRecordFunc(value = "testUserNoReturn")
    public void testUser(Integer id) {
    }
}
