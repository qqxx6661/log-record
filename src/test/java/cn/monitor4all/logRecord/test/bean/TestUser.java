package cn.monitor4all.logRecord.test.bean;

import cn.monitor4all.logRecord.annotation.LogRecordDiff;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@LogRecordDiff(alias = "用户信息实体")
public class TestUser {

    @LogRecordDiff(alias = "用户工号")
    private Integer id;

    @LogRecordDiff
    private String name;

}
