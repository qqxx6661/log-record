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
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.NamedThreadLocal;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Aspect
@Configuration
@Slf4j
public class SystemLogAspect {

    @Autowired
    private LogService logService;

    private static final ThreadLocal<List<LogDTO>> logDTOThreadLocal = new NamedThreadLocal<>("ThreadLocal logDTOList");

    private SpelExpressionParser parser = new SpelExpressionParser();

    private DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @Before("@annotation(cn.monitor4all.logRecord.annotation.OperationLog) || @annotation(cn.monitor4all.logRecord.annotation.OperationLogs)")
    public void doBefore(JoinPoint joinPoint){
        try {
            List<LogDTO> logDTOList = new ArrayList<>();
            logDTOThreadLocal.set(logDTOList);

            Object[] arguments = joinPoint.getArgs();
            Method method = getMethod(joinPoint);
            OperationLog[] annotations = method.getAnnotationsByType(OperationLog.class);
            for (OperationLog annotation: annotations) {
                // 放在最前防止下方逻辑执行异常
                LogDTO logDTO = new LogDTO();
                logDTOList.add(logDTO);
                // 解析Spel表达式
                String[] params = discoverer.getParameterNames(method);
                //获取方法的实际参数值
                EvaluationContext context = new StandardEvaluationContext();
                if (params != null) {
                    for (int len = 0; len < params.length; len++) {
                        context.setVariable(params[len], arguments[len]);
                    }
                }
                //解析表达式并获取spel的值
                String bizIdSpel = annotation.bizId();
                Expression bizIdExpression = parser.parseExpression(bizIdSpel);
                String bizId = bizIdExpression.getValue(context, String.class);

                String msgSpel = annotation.msg();
                String msg = null;
                if (StringUtils.isNotBlank(msgSpel)) {
                    Expression msgExpression = parser.parseExpression(msgSpel);
                    Object msgObj = msgExpression.getValue(context, Object.class);
                    msg = JSON.toJSONString(msgObj, SerializerFeature.WriteMapNullValue);
                }

                // 写入LogDTO
                logDTO.setLogId(UUID.randomUUID().toString());
                logDTO.setSuccess(true);
                logDTO.setBizId(bizId);
                logDTO.setBizType(annotation.bizType());
                logDTO.setOperateDate(new Date());
                logDTO.setMsg(msg);
                logDTO.setTag(annotation.tag());
            }

        } catch (Exception e) {
            log.error("OperationLog doBefore error", e);
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
            log.error("getMethod error", e);
        }
        return method;
    }

    @Around("@annotation(cn.monitor4all.logRecord.annotation.OperationLog) || @annotation(cn.monitor4all.logRecord.annotation.OperationLogs)")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable{
        Object result;
        try {
            result = pjp.proceed();
        } catch (Throwable throwable) {
            List<LogDTO> logDTOList = logDTOThreadLocal.get();
            logDTOList.forEach(logDTO -> {
                logDTO.setSuccess(false);
                logDTO.setException(throwable.getMessage());
            });
            throw throwable;
        }
        finally {
            List<LogDTO> logDTOList = logDTOThreadLocal.get();
            logDTOList.forEach(logDTO -> {
                try {
                    logService.createLog(logDTO);
                } catch (Throwable throwable) {
                    log.error("logRecord send message failure", throwable);
                }
            });
            logDTOThreadLocal.remove();
        }
        return result;
    }
}
