package cn.monitor4all.logRecord.service;

import cn.monitor4all.logRecord.bean.LogDTO;

public abstract class CustomLogListener {

    public abstract void createLog(LogDTO logDTO) throws Exception;

}
