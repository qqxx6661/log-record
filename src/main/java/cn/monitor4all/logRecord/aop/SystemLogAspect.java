package cn.monitor4all.logRecord.aop;

import cn.monitor4all.logRecord.annotation.OperationLog;
import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.context.LogRecordContext;
import cn.monitor4all.logRecord.function.CustomFunctionRegistrar;
import cn.monitor4all.logRecord.service.IOperationLogGetService;
import cn.monitor4all.logRecord.service.IOperatorIdGetService;
import cn.monitor4all.logRecord.service.LogService;
import cn.monitor4all.logRecord.thread.LogRecordThreadPool;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
import java.util.*;
import java.util.function.Consumer;

@Aspect
@Component
@Slf4j
public class SystemLogAspect {

    @Autowired(required = false)
    private LogRecordThreadPool logRecordThreadPool;

    @Autowired(required = false)
    private LogService logService;

    @Autowired(required = false)
    private IOperationLogGetService iOperationLogGetService;

    @Autowired(required = false)
    private IOperatorIdGetService iOperatorIdGetService;

    private final SpelExpressionParser parser = new SpelExpressionParser();

    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(cn.monitor4all.logRecord.annotation.OperationLog) || @annotation(cn.monitor4all.logRecord.annotation.OperationLogs)")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        Object result;
        List<LogDTO> logDTOList = new ArrayList<>();
        Method method = getMethod(pjp);
        OperationLog[] annotations = method.getAnnotationsByType(OperationLog.class);

        // 将前置和后置执行的注解分开处理并保证最终写入顺序
        Map<OperationLog, LogDTO> logDtoMap = new LinkedHashMap<>();

        StopWatch stopWatch = new StopWatch();
        try {
            // 方法执行前
            for (OperationLog annotation : annotations) {
                if (annotation.executeBeforeFunc()) {
                    LogDTO logDTO = resolveExpress(annotation, pjp);
                    if (logDTO != null) {
                        logDtoMap.put(annotation, logDTO);
                    }
                }
            }
            stopWatch.start();
            result = pjp.proceed();
            stopWatch.stop();
            // 方法执行后
            for (OperationLog annotation : annotations) {
                if (!annotation.executeBeforeFunc()) {
                    LogDTO logDTO = resolveExpress(annotation, pjp);
                    if (logDTO != null) {
                        logDtoMap.put(annotation, logDTO);
                    }
                }
            }
            // 写入成功执行结果
            logDTOList = new ArrayList<>(logDtoMap.values());
            logDtoMap.forEach((annotation, logDTO) -> {
                logDTO.setSuccess(true);
                if (annotation.recordReturnValue()) {
                    logDTO.setReturnStr(JSON.toJSONString(result));
                }
            });
        } catch (Throwable throwable) {
            stopWatch.stop();
            // 方法执行异常后
            for (OperationLog annotation : annotations) {
                if (!annotation.executeBeforeFunc()) {
                    logDtoMap.put(annotation, resolveExpress(annotation, pjp));
                }
            }
            // 写入异常执行结果
            logDTOList = new ArrayList<>(logDtoMap.values());
            logDTOList.forEach(logDTO -> {
                logDTO.setSuccess(false);
                logDTO.setException(throwable.getMessage());
            });
            throw throwable;
        } finally {
            // 清除Context：每次方法执行一次
            LogRecordContext.clearContext();
            // 提交logDTO至主线程或线程池
            Consumer<LogDTO> createLogFunction = logDTO -> {
                try {
                    // 记录执行时间
                    logDTO.setExecutionTime(stopWatch.getTotalTimeMillis());
                    // 发送日志本地监听
                    if (iOperationLogGetService != null) {
                        iOperationLogGetService.createLog(logDTO);
                    }
                    // 发送消息管道
                    if (logService != null) {
                        logService.createLog(logDTO);
                    }
                } catch (Throwable throwable) {
                    log.error("Send logDTO error", throwable);
                }
            };
            if (logRecordThreadPool != null) {
                logDTOList.forEach(logDTO -> logRecordThreadPool.getLogRecordPoolExecutor().submit(() -> createLogFunction.accept(logDTO)));
            } else {
                logDTOList.forEach(createLogFunction);
            }
        }
        return result;
    }

    private LogDTO resolveExpress(OperationLog annotation, JoinPoint joinPoint) {
        LogDTO logDTO = null;
        String bizIdSpel = annotation.bizId();
        String msgSpel = annotation.msg();
        String extraSpel = annotation.extra();
        String operatorIdSpel = annotation.operatorId();
        String conditionSpel = annotation.condition();
        String bizId = bizIdSpel;
        String msg = msgSpel;
        String extra = extraSpel;
        String operatorId = annotation.operatorId();
        try {
            Object[] arguments = joinPoint.getArgs();
            Method method = getMethod(joinPoint);
            String[] params = discoverer.getParameterNames(method);
            StandardEvaluationContext context = LogRecordContext.getContext();
            CustomFunctionRegistrar.register(context);
            if (params != null) {
                for (int len = 0; len < params.length; len++) {
                    context.setVariable(params[len], arguments[len]);
                }
            }

            // condition 处理：SpEL解析
            if (StringUtils.isNotBlank(conditionSpel)) {
                Expression conditionExpression = parser.parseExpression(conditionSpel);
                boolean passed = Boolean.TRUE.equals(conditionExpression.getValue(context, Boolean.class));
                if (!passed) {
                    return null;
                }
            }

            // bizId 处理：SpEL解析
            if (StringUtils.isNotBlank(bizIdSpel)) {
                Expression bizIdExpression = parser.parseExpression(bizIdSpel);
                bizId = bizIdExpression.getValue(context, String.class);
            }

            // msg 处理：SpEL解析 默认写入原字符串
            if (StringUtils.isNotBlank(msgSpel)) {
                Expression msgExpression = parser.parseExpression(msgSpel);
                Object msgObj = msgExpression.getValue(context, Object.class);
                msg = msgObj instanceof String ? (String) msgObj : JSON.toJSONString(msgObj, SerializerFeature.WriteMapNullValue);
            }

            // extra 处理：SpEL解析 默认写入原字符串
            if (StringUtils.isNotBlank(extraSpel)) {
                Expression extraExpression = parser.parseExpression(extraSpel);
                Object extraObj = extraExpression.getValue(context, Object.class);
                extra = extraObj instanceof String ? (String) extraObj : JSON.toJSONString(extraObj, SerializerFeature.WriteMapNullValue);
            }

            // operatorId 处理：优先级 注解传入 > 自定义接口实现
            if (iOperatorIdGetService != null) {
                operatorId = iOperatorIdGetService.getOperatorId();
            }
            if (StringUtils.isNotBlank(operatorIdSpel)) {
                Expression operatorIdExpression = parser.parseExpression(operatorIdSpel);
                Object operatorIdObj = operatorIdExpression.getValue(context, Object.class);
                operatorId = operatorIdObj instanceof String ? (String) operatorIdObj : JSON.toJSONString(operatorIdObj, SerializerFeature.WriteMapNullValue);
            }

            logDTO = new LogDTO();
            logDTO.setLogId(UUID.randomUUID().toString());
            logDTO.setBizId(bizId);
            logDTO.setBizType(annotation.bizType());
            logDTO.setTag(annotation.tag());
            logDTO.setOperateDate(new Date());
            logDTO.setMsg(msg);
            logDTO.setExtra(extra);
            logDTO.setOperatorId(operatorId);
            logDTO.setDiffDTOList(LogRecordContext.getDiffDTOList());
        } catch (Exception e) {
            log.error("OperationLogAspect resolveExpress error", e);
        } finally {
            // 清除Diff实体列表：每次注解执行一次
            LogRecordContext.clearDiffDTOList();
        }
        return logDTO;
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
}
