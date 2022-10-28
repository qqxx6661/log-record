package cn.monitor4all.logRecord.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 申明该类参与实体类DIFF
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecordDiffObject {

    /**
     * 类的别名：不填则默认类名
     */
    String alias() default "";

    /**
     * 类中所有字段是否开启DIFF：默认开启
     */
    boolean enableAllFields() default true;
}
