package cn.monitor4all.logRecord.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 申明该方法为为自定义函数
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface LogRecordFunc {

    /**
     * 注册的function命名，便于自定义命名
     * 需要申明@AliasFor以兼容JDK17+，否则会有WARN
     * @return 自定义的名字，如果为空即使用method的命名
     */
    @AliasFor(annotation=Component.class)
    String value() default "";
}
