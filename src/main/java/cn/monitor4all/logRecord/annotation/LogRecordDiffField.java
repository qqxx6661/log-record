package cn.monitor4all.logRecord.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 申明该字段参与实体类DIFF
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecordDiffField {

    /**
     * 字段的别名：不填则默认字段名
     */
    String alias() default "";

    /**
     * 字段是否在DIFF中忽略：默认不忽略
     */
    boolean ignored() default false;
}
