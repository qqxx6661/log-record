package cn.monitor4all.logRecord.test.bean;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TestEnum {

    TYPE1("type1", "枚举1"),
    TYPE2("type2", "枚举2");

    private final String key;
    private final String name;

}
