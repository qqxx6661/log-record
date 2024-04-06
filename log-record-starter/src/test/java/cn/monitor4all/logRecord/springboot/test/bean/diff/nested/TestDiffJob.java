package cn.monitor4all.logRecord.springboot.test.bean.diff.nested;

import lombok.Data;

import java.util.List;

@Data
public class TestDiffJob {

    private Integer jobId;

    private String jobName;

    private List<TestDiffDuty> dutyList;

}
