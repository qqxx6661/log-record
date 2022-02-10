package cn.monitor4all.logRecord.aop;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.service.AysLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
@Slf4j
public class SystemLogAspect {

    @Autowired
    private AysLogService aysLogService;

    @Around("@annotation(cn.monitor4all.logRecord.annotation.OperationLog) || @annotation(cn.monitor4all.logRecord.annotation.OperationLogs)")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        Object result = null;
        List<LogDTO> logS = new ArrayList<>();
        StopWatch stopWatch = new StopWatch();
        Boolean isSuccess = true;
        String errorMsg = null;
        try {
            stopWatch.start();
            result = pjp.proceed();
        } catch (Throwable throwable) {
            isSuccess = false;
            errorMsg = throwable.getMessage();
            throw throwable;
        } finally {
            stopWatch.stop();
            Long executeTime = stopWatch.getTotalTimeMillis();
            aysLogService.aysWriteLog(pjp, result, isSuccess, errorMsg, executeTime);
        }
        return result;
    }
}