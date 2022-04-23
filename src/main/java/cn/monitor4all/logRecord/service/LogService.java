package cn.monitor4all.logRecord.service;

import cn.monitor4all.logRecord.bean.LogDTO;

public interface LogService {

    boolean createLog(LogDTO logDTO);

}
