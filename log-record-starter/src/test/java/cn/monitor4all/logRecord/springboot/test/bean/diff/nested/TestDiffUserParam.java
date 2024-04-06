package cn.monitor4all.logRecord.springboot.test.bean.diff.nested;

import cn.monitor4all.logRecord.annotation.LogRecordDiffObject;
import lombok.Data;

import java.util.List;

@Data
@LogRecordDiffObject(alias = "用户信息嵌套入参实体")
public class TestDiffUserParam {

    private Integer id;

    private String name;

    private Integer age;

    private List<TestDiffJob> jobList;

}
