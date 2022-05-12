package cn.monitor4all.logRecord.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(OperationLogs.class)
public @interface OperationLog {

    String bizId();

    String bizType();

    String msg() default "";

    String tag() default "operation";

    String extra() default "";

    String operatorId() default "";

    boolean executeBeforeFunc() default false;

    boolean recordReturnValue() default true;

    String condition() default "true";
}
