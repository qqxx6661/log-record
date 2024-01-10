package cn.monitor4all.logRecord.springboot3.test.service;


import cn.monitor4all.logRecord.annotation.LogRecordFunc;

@LogRecordFunc("CustomFunctionStatic")
public class CustomFunctionStaticService {

    @LogRecordFunc("testStaticMethodWithCustomName")
    public static String testStaticMethodWithCustomName(){
        return "testStaticMethodWithCustomName";
    }

    @LogRecordFunc
    public static String testStaticMethodWithoutCustomName(){
        return "testStaticMethodWithoutCustomName";
    }

}
