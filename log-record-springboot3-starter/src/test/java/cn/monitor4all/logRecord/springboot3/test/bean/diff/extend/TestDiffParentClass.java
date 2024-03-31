package cn.monitor4all.logRecord.springboot3.test.bean.diff.extend;

import cn.monitor4all.logRecord.annotation.LogRecordDiffObject;
import lombok.Data;

@Data
@LogRecordDiffObject(alias = "DIFF测试类父类")
public class TestDiffParentClass {

    private String paramFromParent;
}
