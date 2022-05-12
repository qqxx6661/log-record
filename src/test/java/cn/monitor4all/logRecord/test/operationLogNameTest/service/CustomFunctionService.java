package cn.monitor4all.logRecord.test.operationLogNameTest.service;


import cn.monitor4all.logRecord.annotation.LogRecordFunc;

@LogRecordFunc("test")
public class CustomFunctionService {

    @LogRecordFunc("testMethodWithCustomName")
    public static String testMethodWithCustomName(){
        return "testMethodWithCustomName";
    }

    @LogRecordFunc
    public static String testMethodWithoutCustomName(){
        return "testMethodWithoutCustomName";
    }

}
