package cn.monitor4all.logRecord.bean;

import lombok.Data;

import java.util.List;

@Data
public class DiffDTO {

    /**
     * 实体类名
     */
    private String oldClassName;

    /**
     * 实体类别名
     */
    private String oldClassAlias;

    /**
     * 实体类名
     */
    private String newClassName;

    /**
     * 实体类别名
     */
    private String newClassAlias;

    /**
     * 字段Diff列表
     */
    private List<DiffFieldDTO> diffFieldDTOList;
}
