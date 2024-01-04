package cn.monitor4all.logRecord.bean;

import lombok.Data;

/**
 * DIFF字段实体
 */
@Data
public class DiffFieldDTO {

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段别名
     */
    private String oldFieldAlias;

    /**
     * 字段别名
     */
    private String newFieldAlias;

    /**
     * 旧值
     */
    private Object oldValue;

    /**
     * 新值
     */
    private Object newValue;
}
