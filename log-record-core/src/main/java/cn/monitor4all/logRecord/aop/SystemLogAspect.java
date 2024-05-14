package cn.monitor4all.logRecord.aop;

import cn.monitor4all.logRecord.annotation.OperationLog;
import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.configuration.LogRecordProperties;
import cn.monitor4all.logRecord.context.LogRecordContext;
import cn.monitor4all.logRecord.function.CustomFunctionRegistrar;
import cn.monitor4all.logRecord.service.DataPipelineService;
import cn.monitor4all.logRecord.service.IOperationLogGetService;
import cn.monitor4all.logRecord.service.IOperatorIdGetService;
import cn.monitor4all.logRecord.service.LogRecordErrorHandlerService;
import cn.monitor4all.logRecord.thread.LogRecordThreadPool;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.ttl.TtlRunnable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Aspect
@Component
@Slf4j
public class SystemLogAspect {

    @Autowired
    private LogRecordProperties logRecordProperties;

    @Autowired(required = false)
    private LogRecordThreadPool logRecordThreadPool;

    @Autowired(required = false)
    private DataPipelineService dataPipelineService;

    @Autowired(required = false)
    private IOperationLogGetService iOperationLogGetService;

    @Autowired(required = false)
    private IOperatorIdGetService iOperatorIdGetService;

    @Autowired(required = false)
    private LogRecordErrorHandlerService logRecordErrorHandlerService;

    private final SpelExpressionParser parser = new SpelExpressionParser();

    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    private static final String ARGS_PREFIX = "p";

    @Around("@annotation(cn.monitor4all.logRecord.annotation.OperationLog) || @annotation(cn.monitor4all.logRecord.annotation.OperationLogs)")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        Object result;
        OperationLog[] annotations;
        List<LogDTO> logDTOList = new ArrayList<>();
        Map<OperationLog, LogDTO> logDtoMap = new LinkedHashMap<>();
        StopWatch stopWatch = null;
        Long executionTime = null;

        // 注解解析：若解析失败直接不执行日志切面逻辑
        try {
            Method method = getMethod(pjp);
            annotations = method.getAnnotationsByType(OperationLog.class);
        } catch (Throwable throwable) {
            return pjp.proceed();
        }

        // 日志切面逻辑
        try {
            // 方法执行前日志切面
            try {
                // 将前置和后置执行的注解分开处理并保证最终写入顺序
                for (OperationLog annotation : annotations) {
                    if (annotation.executeBeforeFunc()) {
                        LogDTO logDTO = resolveExpress(annotation, pjp);
                        if (logDTO != null) {
                            logDtoMap.put(annotation, logDTO);
                        }
                    }
                }
                stopWatch = new StopWatch();
                stopWatch.start();
            } catch (Throwable throwableBeforeFunc) {
                log.error("OperationLogAspect doAround before function, error:", throwableBeforeFunc);
            }
            // 原方法执行
            result = pjp.proceed();
            // 方法成功执行后日志切面
            try {
                if (stopWatch != null) {
                    stopWatch.stop();
                    executionTime = stopWatch.getTotalTimeMillis();
                }
                // 在LogRecordContext中写入执行后信息
                LogRecordContext.putVariable(LogRecordContext.CONTEXT_KEY_NAME_RETURN, result);
                for (OperationLog annotation : annotations) {
                    if (!annotation.executeBeforeFunc()) {
                        LogDTO logDTO = resolveExpress(annotation, pjp);
                        if (logDTO != null) {
                            logDtoMap.put(annotation, logDTO);
                        }
                    }
                }
                // 写入成功执行后日志
                logDTOList = new ArrayList<>(logDtoMap.values());
                logDtoMap.forEach((annotation, logDTO) -> {
                    // 若自定义成功失败，则logDTO.getSuccess非null
                    if (logDTO.getSuccess() == null) {
                        logDTO.setSuccess(true);
                    }
                    if (annotation.recordReturnValue() && result != null) {
                        logDTO.setReturnStr(JSON.toJSONString(result));
                    }
                });
            } catch (Throwable throwableAfterFuncSuccess) {
                log.error("OperationLogAspect doAround after function success, error:", throwableAfterFuncSuccess);
            }

        }
        // 原方法执行异常
        catch (Throwable throwable) {
            // 方法异常执行后日志切面
            try {
                if (stopWatch != null) {
                    stopWatch.stop();
                    executionTime = stopWatch.getTotalTimeMillis();
                }
                // 在LogRecordContext中写入执行后信息
                LogRecordContext.putVariable(LogRecordContext.CONTEXT_KEY_NAME_ERROR_MSG, throwable.getMessage());
                for (OperationLog annotation : annotations) {
                    if (!annotation.executeBeforeFunc()) {
                        LogDTO logDTO = resolveExpress(annotation, pjp);
                        if (logDTO != null) {
                            logDtoMap.put(annotation, logDTO);
                        }
                    }
                }
                // 写入异常执行后日志
                logDTOList = new ArrayList<>(logDtoMap.values());
                logDTOList.forEach(logDTO -> {
                    logDTO.setSuccess(false);
                    logDTO.setException(throwable.getMessage());
                });
            } catch (Throwable throwableAfterFuncFailure) {
                log.error("OperationLogAspect doAround after function failure, error:", throwableAfterFuncFailure);
            }
            // 抛出原方法异常
            throw throwable;
        } finally {
            try {
                // 提交日志至主线程或线程池
                Long finalExecutionTime = executionTime;
                Consumer<LogDTO> createLogFunction = logDTO -> createLog(logDTO, finalExecutionTime);
                if (logRecordThreadPool != null) {
                    logDTOList.forEach(logDTO -> {
                        Runnable task = () -> createLogFunction.accept(logDTO);
                        Runnable ttlRunnable = TtlRunnable.get(task);
                        logRecordThreadPool.getLogRecordPoolExecutor().execute(ttlRunnable);
                    });
                } else {
                    logDTOList.forEach(createLogFunction);
                }
                // 清除Context：每次方法执行一次
                LogRecordContext.clearContext();
            } catch (Throwable throwableFinal) {
                log.error("OperationLogAspect doAround final error", throwableFinal);
            }
        }
        return result;
    }

    private LogDTO resolveExpress(OperationLog annotation, JoinPoint joinPoint) {
        LogDTO logDTO = null;
        String bizIdSpel = annotation.bizId();
        String bizTypeSpel = annotation.bizType();
        String tagSpel = annotation.tag();
        String msgSpel = annotation.msg();
        String extraSpel = annotation.extra();
        String operatorIdSpel = annotation.operatorId();
        String conditionSpel = annotation.condition();
        String successSpel = annotation.success();
        String bizId = null;
        String bizType = null;
        String tag = null;
        String msg = null;
        String extra = null;
        String operatorId = null;
        Boolean functionExecuteSuccess = null;
        try {
            Object[] arguments = joinPoint.getArgs();
            Method method = getMethod(joinPoint);
            String[] params = discoverer.getParameterNames(method);
            StandardEvaluationContext context = LogRecordContext.getContext();
            CustomFunctionRegistrar.register(context);

            // 若能够获取参数名则使用参数名作为key
            if (params != null) {
                for (int len = 0; len < params.length; len++) {
                    context.setVariable(params[len], arguments[len]);
                }
            }
            // 若不能获取参数名则使用参数下标作为key 通常是由于JDk反射限制导致参数名无法获取
            else {
                for (int len = 0; len < arguments.length; len++) {
                    context.setVariable(ARGS_PREFIX + len, arguments[len]);
                }
            }

            // condition 处理：SpEL解析 必须符合表达式
            if (StringUtils.isNotBlank(conditionSpel)) {
                boolean conditionPassed = parseParamToBoolean(conditionSpel, context);
                if (!conditionPassed) {
                    return null;
                }
            }

            // success 处理：SpEL解析 必须符合表达式
            if (StringUtils.isNotBlank(successSpel)) {
                functionExecuteSuccess = parseParamToBoolean(successSpel, context);
            }

            // bizId 处理：SpEL解析 必须符合表达式
            if (StringUtils.isNotBlank(bizIdSpel)) {
                bizId = parseParamToString(bizIdSpel, context);
            }

            // bizType 处理：SpEL解析 必须符合表达式
            if (StringUtils.isNotBlank(bizTypeSpel)) {
                bizType = parseParamToString(bizTypeSpel, context);
            }

            // tag 处理：SpEL解析 必须符合表达式
            if (StringUtils.isNotBlank(tagSpel)) {
                tag = parseParamToString(tagSpel, context);
            }

            // msg 处理：SpEL解析 必须符合表达式 若为实体则JSON序列化实体 若为空则返回null
            if (StringUtils.isNotBlank(msgSpel)) {
                msg = parseParamToStringOrJson(msgSpel, context);
            }

            // extra 处理：SpEL解析 必须符合表达式 若为实体则JSON序列化实体 若为空则返回null
            if (StringUtils.isNotBlank(extraSpel)) {
                extra = parseParamToStringOrJson(extraSpel, context);
            }

            // operatorId 处理：优先级 注解传入 > 自定义接口实现
            if (iOperatorIdGetService != null) {
                operatorId = iOperatorIdGetService.getOperatorId();
            }
            if (StringUtils.isNotBlank(operatorIdSpel)) {
                operatorId = parseParamToString(operatorIdSpel, context);
            }

            logDTO = new LogDTO();
            logDTO.setLogId(UUID.randomUUID().toString());
            logDTO.setBizId(bizId);
            logDTO.setBizType(bizType);
            logDTO.setTag(tag);
            logDTO.setOperateDate(new Date());
            logDTO.setMsg(msg);
            logDTO.setExtra(extra);
            logDTO.setOperatorId(operatorId);
            logDTO.setSuccess(functionExecuteSuccess);
            logDTO.setDiffDTOList(LogRecordContext.getDiffDTOList());
        } catch (Exception e) {
            log.error("OperationLogAspect resolveExpress error", e);
        } finally {
            // 清除Diff实体列表：每次注解执行一次
            LogRecordContext.clearDiffDTOList();
        }
        return logDTO;
    }

    private boolean parseParamToBoolean(String spel, StandardEvaluationContext context) {
        Expression conditionExpression = parser.parseExpression(spel);
        return Boolean.TRUE.equals(conditionExpression.getValue(context, Boolean.class));
    }

    private String parseParamToString(String spel, StandardEvaluationContext context) {
        Expression bizIdExpression = parser.parseExpression(spel);
        return bizIdExpression.getValue(context, String.class);
    }

    private String parseParamToStringOrJson(String spel, StandardEvaluationContext context) {
        Expression msgExpression = parser.parseExpression(spel);
        Object obj = msgExpression.getValue(context, Object.class);
        if (obj != null) {
            return obj instanceof String ? (String) obj : JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue);
        }
        return null;
    }

    private Method getMethod(JoinPoint joinPoint) {
        Method method = null;
        try {
            Signature signature = joinPoint.getSignature();
            MethodSignature ms = (MethodSignature) signature;
            Object target = joinPoint.getTarget();
            method = target.getClass().getMethod(ms.getName(), ms.getParameterTypes());
        } catch (NoSuchMethodException e) {
            log.error("OperationLogAspect getMethod error", e);
        }
        return method;
    }

    private void createLog(LogDTO logDTO, Long finalExecutionTime) {
        int maxRetryTimes = logRecordProperties.getRetry().getRetryTimes();

        // 发送日志本地监听
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

        if (!dataPipelineServiceResult && dataPipelineService != null && logRecordErrorHandlerService != null) {
            logRecordErrorHandlerService.dataPipelineErrorHandler();
        }

    }
}
