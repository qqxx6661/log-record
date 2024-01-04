package cn.monitor4all.logRecord.springboot3.test.bean.diff;

import lombok.Data;

import java.util.List;

@Data
public class TestDiffJob {

    private Integer jobId;

    private String jobName;

    private List<TestDiffDuty> dutyList;

}
