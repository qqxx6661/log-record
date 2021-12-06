package cn.monitor4all.logRecord.aop;

import cn.monitor4all.logRecord.annotation.OperationLog;
import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.service.LogService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.NamedThreadLocal;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Aspect
@Component
@Slf4j
public class SystemLogAspect {

    @Autowired
    private LogService logService;

    private static final ThreadLocal<List<LogDTO>> LOGDTO_THREAD_LOCAL = new NamedThreadLocal<>("ThreadLocal logDTOList");

    private final SpelExpressionParser parser = new SpelExpressionParser();

    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @Before("@annotation(cn.monitor4all.logRecord.annotation.OperationLog) || @annotation(cn.monitor4all.logRecord.annotation.OperationLogs)")
    public void doBefore(JoinPoint joinPoint){
        try {

            List<LogDTO> logDTOList = new ArrayList<>();
            LOGDTO_THREAD_LOCAL.set(logDTOList);

            Object[] arguments = joinPoint.getArgs();
            Method method = getMethod(joinPoint);
            OperationLog[] annotations = method.getAnnotationsByType(OperationLog.class);

            // 批量处理注解
            for (OperationLog annotation: annotations) {

                // 初始化logDTO
                LogDTO logDTO = new LogDTO();
                logDTOList.add(logDTO);
                String bizIdSpel = annotation.bizId();
                String msgSpel = annotation.msg();
                String bizId = bizIdSpel;
                String extraMsg = msgSpel;

                try {
                    String[] params = discoverer.getParameterNames(method);
                    EvaluationContext context = new StandardEvaluationContext();
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
                    // extraMsg 处理，写入默认传入的字符串
                    if (StringUtils.isNotBlank(msgSpel)) {
                        Expression msgExpression = parser.parseExpression(msgSpel);
                        Object msgObj = msgExpression.getValue(context, Object.class);
                        extraMsg = JSON.toJSONString(msgObj, SerializerFeature.WriteMapNullValue);
                    }

                } catch (Exception e) {
                    log.error("SystemLogAspect doBefore error", e);
                } finally {
                    logDTO.setLogId(UUID.randomUUID().toString());
                    logDTO.setSuccess(true);
                    logDTO.setBizId(bizId);
                    logDTO.setBizType(annotation.bizType());
                    logDTO.setOperateDate(new Date());
                    logDTO.setMsg(extraMsg);
                    logDTO.setTag(annotation.tag());
                }
            }

        } catch (Exception e) {
            log.error("SystemLogAspect doBefore error", e);
        }
    }

    protected Method getMethod(JoinPoint joinPoint) {
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

    @Around("@annotation(cn.monitor4all.logRecord.annotation.OperationLog) || @annotation(cn.monitor4all.logRecord.annotation.OperationLogs)")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable{
        Object result;
        try {
            result = pjp.proceed();
            // logDTO写入返回值信息 若方法抛出异常，则不会走入下方逻辑
            List<LogDTO> logDTOList = LOGDTO_THREAD_LOCAL.get();
            String returnStr = JSON.toJSONString(result);
            logDTOList.forEach(logDTO -> logDTO.setReturnStr(returnStr));
        } catch (Throwable throwable) {
            // logDTO写入异常信息
            List<LogDTO> logDTOList = LOGDTO_THREAD_LOCAL.get();
            logDTOList.forEach(logDTO -> {
                logDTO.setSuccess(false);
                logDTO.setException(throwable.getMessage());
            });
            throw throwable;
        }
        finally {
            // logDTO发送至数据管道
            List<LogDTO> logDTOList = LOGDTO_THREAD_LOCAL.get();
            logDTOList.forEach(logDTO -> {
                try {
                    logService.createLog(logDTO);
                } catch (Throwable throwable) {
                    log.error("logRecord send message failure", throwable);
                }
            });
            LOGDTO_THREAD_LOCAL.remove();
        }
        return result;
    }
}
