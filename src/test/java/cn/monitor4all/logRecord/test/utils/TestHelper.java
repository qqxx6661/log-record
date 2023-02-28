package cn.monitor4all.logRecord.test.utils;


import cn.monitor4all.logRecord.bean.LogDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TestHelper {

    private static final Map<String, CountDownLatch> LATCH_MAP = new LinkedHashMap<>();

    private static final Map<String, LogDTO> LOG_DTO_MAP = new LinkedHashMap<>();

    public static void addLock(String key) {
        CountDownLatch latch = LATCH_MAP.get(key);
        if (latch == null) {
            latch = new CountDownLatch(1);
            LATCH_MAP.put(key, latch);
        }
    }

    public static void releaseLock(String key) {
        CountDownLatch latch = LATCH_MAP.get(key);
        if (latch != null) {
            latch.countDown();
        }
    }

    public static void await(String key) {
        CountDownLatch latch = LATCH_MAP.get(key);
        if (latch != null) {
            try {
                boolean result = latch.await(3000, TimeUnit.MILLISECONDS);
                if (!result) {
                    log.error("await timeout");
                }
            } catch (InterruptedException e) {
                log.error("await error", e);
            }
        }
    }

    public static void putLogDTO(String key, LogDTO logDTO) {
        LOG_DTO_MAP.put(key, logDTO);
    }

    public static LogDTO getLogDTO(String key) {
        return LOG_DTO_MAP.get(key);
    }
}
