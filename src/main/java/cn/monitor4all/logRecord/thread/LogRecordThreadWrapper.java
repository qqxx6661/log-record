package cn.monitor4all.logRecord.thread;

import cn.monitor4all.logRecord.bean.LogDTO;

import java.util.function.Consumer;

/**
 *
 * 业务中有可能需要包装任务,将trace id传递给多线程执行的任务
 * ex:
 * <p>@Configuration<br>
 * <p>public class LogRecordConfig<br>
 * <p>&emsp;&emsp;@Bean<br>
 * <p>&emsp;&emsp;public LogRecordThreadWrapper logRecordThreadWrapper() {<br>
 * <p>&emsp;&emsp;&emsp;&emsp;return new LogRecordThreadWrapper() {<br>
 * <p>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;@Override<br>
 * <p>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;public Runnable createLog(Consumer&#60;LogDTO&#62; consumer, LogDTO logDTO) {<br>
 * <p>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;return RunnableWrapper.of(new LogRecordThreadWrapper(){}.createLog(consumer, logDTO));<br>
 * <p>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;}<br>
 * <p>&emsp;&emsp;&emsp;&emsp;};<br>
 * <p>&emsp;&emsp;}<br>
 * <p>}<br>
 * @author : dechao.mao
 */
public interface LogRecordThreadWrapper {

    /**
     * 暴露多线程执行任务,方便业务层对多线程任务包装
     * 也可以在任务前后执行一些动作
     * @param consumer            日志处理业务
     * @param logDTO              日志输出对象
     * @return                    输出日志任务
     */
    default Runnable createLog(Consumer<LogDTO> consumer, LogDTO logDTO) {
        return () -> consumer.accept(logDTO);
    }

}
