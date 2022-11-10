package cn.monitor4all.logRecord.function;

import cn.monitor4all.logRecord.annotation.LogRecordFunc;
import javassist.*;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

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
                            // 获取真实类
                            Object originalClass = getTarget(component);
                            LogRecordFunc classLogRecordFunc = originalClass.getClass().getAnnotation(LogRecordFunc.class);
                            StringBuilder prefixName = new StringBuilder(classLogRecordFunc.value());
                            if (StringUtils.hasText(prefixName)) {
                                prefixName.append("_");
                            }
                            if (methods.length > 0) {
                                Map<Method, LogRecordFunc> annotationMap = new HashMap<>();
                                for (Method method : methods) {
                                    LogRecordFunc annotation = AnnotatedElementUtils.findMergedAnnotation(method, LogRecordFunc.class);
                                    if (annotation != null) {
                                        annotationMap.put(method, annotation);
                                    }
                                }
                                // 静态方法直接调用
                                annotationMap.keySet().stream().filter(CustomFunctionRegistrar::isStaticMethod).forEach(method -> {
                                    LogRecordFunc annotation = annotationMap.get(method);
                                    String registerName = StringUtils.hasText(annotation.value()) ? annotation.value() : method.getName();
                                    functionMap.put(prefixName + registerName, method);
                                    log.info("LogRecord register static custom function [{}] as name [{}]", method, prefixName + registerName);
                                });
                                // 非静态方法需要包装为静态方法
                                List<Method> nonStaticMethods = annotationMap.keySet().stream().filter(method -> !isStaticMethod(method)).collect(Collectors.toList());
                                try {
                                    // 包装后注解信息丢失, 需要使用键值对存储返回, 一个类只需要组装一次, 不必将所有方法代理, 只挑待处理的
                                    proxy2static(nonStaticMethods, component, originalClass).forEach(staticProxyMethod -> {
                                        annotationMap.keySet().forEach(method -> {
                                            if(Arrays.equals(method.getParameterTypes(), staticProxyMethod.getParameterTypes()) && method.getName().equals(staticProxyMethod.getName())) {
                                                LogRecordFunc annotation = annotationMap.get(method);
                                                String registerName = StringUtils.hasText(annotation.value()) ? annotation.value() : method.getName();
                                                functionMap.put(prefixName + registerName, staticProxyMethod);
                                                log.info("LogRecord register nonstatic custom function [{}] as name [{}]", method, prefixName + registerName);
                                            }
                                        });

                                    });
                                } catch (NotFoundException | CannotCompileException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                                    log.error(e.getMessage(), e);
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                );
    }

    @SneakyThrows
    private List<Method> proxy2static(List<Method> nonStaticMethods, Object delegate, Object originalClass) throws NotFoundException, CannotCompileException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ClassPool pool = ClassPool.getDefault();
        Class<?> targetClass = originalClass.getClass();

        // 委派类名称
        String staticallyClassName = targetClass.getName()+"_"+"Statically";
        // jvm class 委派类,同名类只能加载一次
        Class<?> delegateClass = null;
        // 查看Javassist之前是否加载过，加载过不再重复构建类
        CtClass ctClass = pool.get(staticallyClassName);
        if(ctClass == null) {
            ctClass = constructCtClass(nonStaticMethods, pool, targetClass, staticallyClassName);
            delegateClass = ctClass.toClass();
        }else {
            delegateClass = ctClass.getClass().getClassLoader().loadClass(staticallyClassName);
        }
        Object proxy = delegateClass.getConstructor(targetClass).newInstance(delegate);
        return Arrays.asList(proxy.getClass().getDeclaredMethods());
    }

    private CtClass constructCtClass(List<Method> nonStaticMethods, ClassPool pool, Class<?> targetClass, String staticallyClassName) throws NotFoundException, CannotCompileException {
        CtClass ctClass;
        ctClass = pool.makeClass(staticallyClassName);
        ctClass.addInterface(pool.get(Serializable.class.getName()));

        CtField field = new CtField(pool.get(targetClass.getName()), "delegating", ctClass);
        field.setModifiers(javassist.Modifier.STATIC | javassist.Modifier.PROTECTED);
        ctClass.addField(field);

        CtConstructor cons = new CtConstructor(new CtClass[]{pool.get(targetClass.getName())}, ctClass);
        cons.setBody("{delegating = $1;}");
        ctClass.addConstructor(cons);

        CtMethod getter = new CtMethod(pool.get(targetClass.getName()), "getDelegating", new CtClass[]{}, ctClass);
        getter.setModifiers(javassist.Modifier.PUBLIC);
        getter.setBody("{return delegating;}");

        ctClass.addMethod(getter);

        for (Method declaredMethod : nonStaticMethods) {
            int modifier = declaredMethod.getModifiers();
            modifier |= javassist.Modifier.STATIC;
            String methodName = declaredMethod.getName();
            Class<?> returnType = declaredMethod.getReturnType();
            Class<?>[] parameterType = declaredMethod.getParameterTypes();
            StringBuilder builder = new StringBuilder();
            // # modifier static returnType methodName(ParameterType[x] handler)
            builder.append(chooseModifier(modifier)).append(" ").append(returnType.getName()).append(" ").append(methodName).append("(");
            StringBuilder params = null;
            for (int i = 0; i < parameterType.length; i++) {
                builder.append(parameterType[i].getName()).append(" ");
                builder.append("$_").append(i).append(",");
                if (params == null) {
                    params = new StringBuilder();
                }
                params.append("$_").append(i).append(",");
            }
            if (params != null) {
                builder.delete(builder.length() - 1, builder.length());
                params.delete(params.length() - 1, params.length());
            }
            builder.append(")");
            builder.append("{").append("");
            if(!returnType.equals(void.class)) {
                builder.append("return").append(" ");
            }
            builder.append("delegating.").append(methodName).append("(");
            if (params != null) {
                builder.append(params);
            }
            builder.append(")").append(";");
            builder.append("}");

            CtMethod method = CtMethod.make(builder.toString(), ctClass);
            ctClass.addMethod(method);
        }
        return ctClass;
    }

    private String chooseModifier(int modifier) {
        StringBuilder builder = new StringBuilder();
        if ((modifier & javassist.Modifier.PUBLIC) == javassist.Modifier.PUBLIC) {
            builder.append("public").append(" ");
        }
        if ((modifier & javassist.Modifier.PRIVATE) == javassist.Modifier.PRIVATE) {
            builder.append("private").append(" ");
        }
        if ((modifier & javassist.Modifier.PROTECTED) == javassist.Modifier.PROTECTED) {
            builder.append("protected").append(" ");
        }
        if ((modifier & javassist.Modifier.ABSTRACT) == javassist.Modifier.ABSTRACT) {
            builder.append("abstract").append(" ");
        }
        if ((modifier & javassist.Modifier.STATIC) == javassist.Modifier.STATIC) {
            builder.append("static").append(" ");
        }
        if ((modifier & javassist.Modifier.FINAL) == javassist.Modifier.FINAL) {
            builder.append("final").append(" ");
        }
        return builder.toString();
    }



    public static void register(StandardEvaluationContext context) {
        functionMap.forEach(context::registerFunction);
    }

    public static Object getTarget(Object obj ){
        if(!AopUtils.isAopProxy(obj)){
            return  obj;
        }
        try {
            //判断是jdk还是cglib代理
            if (AopUtils.isJdkDynamicProxy(obj)) {
                obj = getJdkDynamicProxyTargetObject(obj);
            } else {
                obj = getCglibDynamicProxyTargetObject(obj);
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return obj;

    }

    private static Object getCglibDynamicProxyTargetObject(Object obj) throws Exception {
        Field h = obj.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);

        Object dynamicAdvisedInterceptor = h.get(obj);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        return ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
    }

    private static Object getJdkDynamicProxyTargetObject(Object obj) throws Exception {

        Field h = obj.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);

        AopProxy aopProxy = (AopProxy)h.get(obj);
        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        return ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();

    }

    /**
     * 判断是否为静态方法
     * @param method    待判断的方法
     * @return  如果为静态方法 返回true 反之false
     */
    private static boolean isStaticMethod(Method method) {
        if (method == null) {
            return false;
        }
        int modifiers = method.getModifiers();
        return Modifier.isStatic(modifiers);
    }
}
