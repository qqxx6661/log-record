package cn.monitor4all.logRecord.springboot.test.bean;

import cn.monitor4all.logRecord.annotation.LogRecordDiffField;
import cn.monitor4all.logRecord.annotation.LogRecordDiffObject;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@LogRecordDiffObject(alias = "用户信息实体", enableAllFields = false)
public class TestUser {

    @LogRecordDiffField(alias = "用户工号")
    private Integer id;

    @LogRecordDiffField
    private String name;

}
