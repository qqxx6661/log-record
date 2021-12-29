package cn.monitor4all.logRecord.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Classname FunctionRegistrar
 * Description SpringEL注册自定义函数
 */
@Data
@Slf4j
public class CustomFunctionRegistrar implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private static List<Method> functions = new ArrayList<>();

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
                            Arrays.stream(methods)
                                    .filter(method -> method.isAnnotationPresent(LogRecordFunc.class))
                                    .forEach(m -> {
                                        functions.add(m);
                                        log.info("LogRecord register custom function [{}]", m);
                                    });
                        }
                );
    }

    public static void register(StandardEvaluationContext context) {
        functions.forEach(m -> context.registerFunction(m.getName(), m));
    }
}
