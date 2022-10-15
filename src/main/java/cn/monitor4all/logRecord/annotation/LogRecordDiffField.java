package cn.monitor4all.logRecord.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注在字段中用于表示该字段需要进行DIFF
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecordDiffField {

    /**
     * 字段的别名：不填则默认字段名
     */
    String alias() default "";
}
