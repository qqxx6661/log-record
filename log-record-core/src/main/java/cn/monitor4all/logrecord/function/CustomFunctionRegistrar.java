package cn.monitor4all.logRecord.function;

import cn.monitor4all.logRecord.annotation.LogRecordFunc;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public class CustomFunctionRegistrar implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private static Map<String, Method> functionMap = new HashMap<>();

    /**
     * 扫描申明的自定义函数
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        Map<String, Object> beanWithAnnotation = applicationContext.getBeansWithAnnotation(LogRecordFunc.class);
        beanWithAnnotation.values()
                .forEach(
                        component -> {
                            Method[] methods = component.getClass().getMethods();
                            LogRecordFunc classLogRecordFunc = AnnotationUtils.findAnnotation(component.getClass(), LogRecordFunc.class);
                            String prefixName = "";
                            if (classLogRecordFunc != null) {
                                prefixName = classLogRecordFunc.value();
                                if (StringUtils.hasText(prefixName)) {
                                    prefixName += "_";
                                }
                            }
                            if (methods.length > 0) {
                                for (Method method : methods) {
                                    if (method.isAnnotationPresent(LogRecordFunc.class)) {
                                        LogRecordFunc logRecordFunc = method.getAnnotation(LogRecordFunc.class);
                                        String registerName = StringUtils.hasText(logRecordFunc.value()) ? logRecordFunc.value() : method.getName();
                                        functionMap.put(prefixName + registerName, method);
                                        log.info("LogRecord register custom function [{}] as name [{}]", method, prefixName + registerName);
                                    }
                                }
                            }
                        }
                );
    }

    public static void register(StandardEvaluationContext context) {
        functionMap.forEach(context::registerFunction);
    }
}
