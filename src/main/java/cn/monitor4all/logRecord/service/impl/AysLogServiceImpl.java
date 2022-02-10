package cn.monitor4all.logRecord.service.impl;

import cn.monitor4all.logRecord.annotation.OperationLog;
import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.context.LogRecordContext;
import cn.monitor4all.logRecord.function.CustomFunctionRegistrar;
import cn.monitor4all.logRecord.service.AysLogService;
import cn.monitor4all.logRecord.service.CustomLogListener;
import cn.monitor4all.logRecord.service.LogService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author pumbf
 * @version 1.0
 * @since 2022-01-24 15:08
 */
@Service
@Slf4j
public class AysLogServiceImpl implements AysLogService {

    @Autowired(required = false)
    private LogService logService;

    @Autowired(required = false)
    private CustomLogListener customLogListener;

    private final SpelExpressionParser parser = new SpelExpressionParser();

    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @Async
    @Override
    public void aysWriteLog(ProceedingJoinPoint pjp, Object result, Boolean isSuccess, String errorMsg, Long executionTime) {
        List<LogDTO> logS = new ArrayList<>();
        if (isSuccess) {
            logS = resolveExpress(pjp, result);
        } else {
            logS = resolveExpress(pjp, null);
        }
        logS.forEach(logDTO -> {
            try {
                logDTO.setSuccess(isSuccess);
                logDTO.setException(errorMsg);
                // 记录执行时间
                logDTO.setExecutionTime(executionTime);
                // 发送本地监听
                if (customLogListener != null) {
                    customLogListener.createLog(logDTO);
                }
                // 发送数据管道
                if (logService != null) {
                    logService.createLog(logDTO);
                }
            } catch (Throwable throwable) {
                log.error("SystemLogAspect doAround send log error", throwable);
            }
        });
        // 清除变量上下文
        LogRecordContext.clearContext();
    }


    public List<LogDTO> resolveExpress(JoinPoint joinPoint, Object returnObj) {
        try {
            List<LogDTO> logDTOList = new ArrayList<>();
            Object[] arguments = joinPoint.getArgs();
            Method method = getMethod(joinPoint);
            OperationLog[] annotations = method.getAnnotationsByType(OperationLog.class);
            for (OperationLog annotation : annotations) {
                LogDTO logDTO = new LogDTO();
                logDTOList.add(logDTO);
                String bizIdSpel = annotation.bizId();
                String msgSpel = annotation.msg();
                String bizId = bizIdSpel;
                String extraMsg = msgSpel;
                String returnMsg = null;
                try {
                    String[] params = discoverer.getParameterNames(method);
                    StandardEvaluationContext context = LogRecordContext.getContext();
                    CustomFunctionRegistrar.register(context);
                    if (params != null) {
                        for (int len = 0; len < params.length; len++) {
                            context.setVariable(params[len], arguments[len]);
                        }
                    }

                    // bizId 处理：直接传入字符串会抛出异常，写入默认传入的字符串
                    if (StringUtils.isNotBlank(bizIdSpel)) {
                        Expression bizIdExpression = parser.parseExpression(bizIdSpel);
                        bizId = bizIdExpression.getValue(context, String.class);
                    }

                    // extraMsg 处理：写入默认传入的字符串
                    if (StringUtils.isNotBlank(msgSpel)) {
                        Expression msgExpression = parser.parseExpression(msgSpel);
                        Object msgObj = msgExpression.getValue(context, Object.class);
                        extraMsg = JSON.toJSONString(msgObj, SerializerFeature.WriteMapNullValue);
                    }

                    // returnObj 处理
                    returnMsg = JSON.toJSONString(returnObj);

                } catch (Exception e) {
                    log.error("SystemLogAspect resolveExpress error", e);
                } finally {
                    logDTO.setLogId(UUID.randomUUID().toString());
                    logDTO.setSuccess(true);
                    logDTO.setBizId(bizId);
                    logDTO.setBizType(annotation.bizType());
                    logDTO.setOperateDate(new Date());
                    logDTO.setMsg(extraMsg);
                    logDTO.setTag(annotation.tag());
                    logDTO.setReturnStr(returnMsg);
                }
            }
            return logDTOList;

        } catch (Exception e) {
            log.error("SystemLogAspect resolveExpress error", e);
            return new ArrayList<>();
        }
    }

    private Method getMethod(JoinPoint joinPoint) {
        Method method = null;
        try {
            Signature signature = joinPoint.getSignature();
            MethodSignature ms = (MethodSignature) signature;
            Object target = joinPoint.getTarget();
            method = target.getClass().getMethod(ms.getName(), ms.getParameterTypes());
        } catch (NoSuchMethodException e) {
            log.error("SystemLogAspect getMethod error", e);
        }
        return method;
    }
}