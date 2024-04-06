package cn.monitor4all.logRecord.springboot.test.bean.diff.extend;

import cn.monitor4all.logRecord.annotation.LogRecordDiffObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@LogRecordDiffObject(alias = "DIFF测试类子类")
public class TestDiffChildClass extends TestDiffParentClass {

    private String paramFromChild;
}
