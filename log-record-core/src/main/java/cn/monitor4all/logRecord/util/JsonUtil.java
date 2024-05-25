package cn.monitor4all.logRecord.util;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil {

    public static String safeToJsonString(Object object) {
        try {
            return JSON.toJSONString(object);
        } catch (Exception e) {
            log.error("safeToJsonString error, object {}", object, e);
            return object.toString();
        }
    }
}
