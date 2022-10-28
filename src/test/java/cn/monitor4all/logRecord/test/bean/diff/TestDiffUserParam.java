package cn.monitor4all.logRecord.test.bean.diff;

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
