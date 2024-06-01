package cn.monitor4all.logRecord.util;


import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.bean.LogRequest;
import cn.monitor4all.logRecord.context.LogRecordContext;
import cn.monitor4all.logRecord.handler.OperationLogHandler;
import cn.monitor4all.logRecord.thread.LogRecordThreadPool;
import com.alibaba.ttl.TtlRunnable;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * 操作日志工具
 */
@Slf4j
public class OperationLogUtil {

    private static final OperationLogHandler operationLogHandler = SpringContextUtil.getBean(OperationLogHandler.class);
    private static final LogRecordThreadPool logRecordThreadPool = SpringContextUtil.getBean(LogRecordThreadPool.class);

    /**
     * 生成LogDTO并交由框架统一处理
     */
    public static void log(LogRequest logRequest) {

        try {
            LogDTO logDTO = generateLogDTO(logRequest);
            Consumer<LogDTO> createLogFunction = log -> operationLogHandler.createLog(log, logRequest.getExecutionTime());
            if (logRecordThreadPool != null) {
                    Runnable task = () -> createLogFunction.accept(logDTO);
                    Runnable ttlRunnable = TtlRunnable.get(task);
                    logRecordThreadPool.getLogRecordPoolExecutor().execute(ttlRunnable);
            } else {
                operationLogHandler.createLog(logDTO, logRequest.getExecutionTime());
            }
            // 清除Context：每次方法执行一次
            LogRecordContext.clearContext();
        } catch (Throwable throwableFinal) {
            log.error("OperationLogAspect send logDTO error", throwableFinal);
        }
    }

    private static LogDTO generateLogDTO(LogRequest logRequest) {
        LogDTO logDTO = new LogDTO();
        logDTO.setBizId(logRequest.getBizId());
        logDTO.setBizType(logRequest.getBizType());
        logDTO.setException(logRequest.getException());
        logDTO.setOperateDate(logRequest.getOperateDate());
        logDTO.setSuccess(logRequest.getSuccess());
        logDTO.setMsg(logRequest.getMsg());
        logDTO.setTag(logRequest.getTag());
        logDTO.setReturnStr(logRequest.getReturnStr());
        logDTO.setExecutionTime(logRequest.getExecutionTime());
        logDTO.setExtra(logRequest.getExtra());
        logDTO.setOperatorId(logRequest.getOperatorId());
        return logDTO;
    }


}
