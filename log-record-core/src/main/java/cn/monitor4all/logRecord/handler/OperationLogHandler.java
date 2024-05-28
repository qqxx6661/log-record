package cn.monitor4all.logRecord.handler;


import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.configuration.LogRecordProperties;
import cn.monitor4all.logRecord.service.DataPipelineService;
import cn.monitor4all.logRecord.service.IOperationLogGetService;
import cn.monitor4all.logRecord.service.LogRecordErrorHandlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OperationLogHandler {

    @Autowired
    private LogRecordProperties logRecordProperties;

    @Autowired(required = false)
    private DataPipelineService dataPipelineService;

    @Autowired(required = false)
    private IOperationLogGetService iOperationLogGetService;

    @Autowired(required = false)
    private LogRecordErrorHandlerService logRecordErrorHandlerService;

    public void createLog(LogDTO logDTO, Long finalExecutionTime) {

        // 重试次数
        int maxRetryTimes = logRecordProperties.getRetry().getRetryTimes();

        // 发送本地日志
        boolean iOperationLogGetResult = false;
        if (iOperationLogGetService != null) {
            for (int retryTimes = 0; retryTimes <= maxRetryTimes; retryTimes++) {
                try {
                    logDTO.setExecutionTime(finalExecutionTime);
                    iOperationLogGetResult = iOperationLogGetService.createLog(logDTO);
                    if (iOperationLogGetResult) {
                        break;
                    }
                } catch (Throwable throwable) {
                    log.error("OperationLogAspect send logDTO error", throwable);
                }
            }
        }

        // 发送本地日志失败错误处理
        if (!iOperationLogGetResult && iOperationLogGetService != null && logRecordErrorHandlerService != null) {
            logRecordErrorHandlerService.operationLogGetErrorHandler();
        }

        // 发送消息管道
        boolean dataPipelineServiceResult = false;
        if (dataPipelineService != null) {
            for (int retryTimes = 0; retryTimes <= maxRetryTimes; retryTimes++) {
                try {
                    logDTO.setExecutionTime(finalExecutionTime);
                    dataPipelineServiceResult = dataPipelineService.createLog(logDTO);
                    if (dataPipelineServiceResult) {
                        break;
                    }
                } catch (Throwable throwable) {
                    log.error("OperationLogAspect send logDTO error", throwable);
                }
            }
        }

        // 发送消息管道失败错误处理
        if (!dataPipelineServiceResult && dataPipelineService != null && logRecordErrorHandlerService != null) {
            logRecordErrorHandlerService.dataPipelineErrorHandler();
        }

    }
}
