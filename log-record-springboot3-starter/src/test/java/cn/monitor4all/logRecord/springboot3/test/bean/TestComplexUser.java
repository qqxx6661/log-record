package cn.monitor4all.logRecord.springboot3.test.bean;

import cn.monitor4all.logRecord.annotation.LogRecordDiffField;
import cn.monitor4all.logRecord.annotation.LogRecordDiffObject;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@LogRecordDiffObject(alias = "用户信息复杂实体")
public class TestComplexUser {

    @LogRecordDiffField(alias = "用户工号")
    private Integer id;

    @LogRecordDiffField
    private String name;

    private Integer age;

    private List<String> nickNameList;

    @LogRecordDiffField(ignored = true)
    private String job;

}
