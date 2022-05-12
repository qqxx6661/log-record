package cn.monitor4all.logRecord.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface LogRecordFunc {

    /**
     * 自定义函数的别名，如果为空即使用函数名
     */
    String value() default "";
}
