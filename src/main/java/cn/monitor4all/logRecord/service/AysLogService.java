package cn.monitor4all.logRecord.service;

import org.aspectj.lang.ProceedingJoinPoint;

/***
 *  异步写入log
 */
public interface AysLogService {

    void aysWriteLog(ProceedingJoinPoint pjp, Object result, Boolean isSuccess, String errorMsg, Long executionTime) throws InterruptedException;

}