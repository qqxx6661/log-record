package cn.monitor4all.logRecord.annotation;

/**
 * @author yangzhendong
 */

public enum DataPipelineEnum {

    QUEUE("queue", "消息队列"),
    DATABASE("database", "数据库");

    private String pipelineName;
    private String pipelineNote;

    private DataPipelineEnum(String pipelineName, String pipelineNote) {
        this.pipelineName = pipelineName;
        this.pipelineNote = pipelineNote;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public String getPipelineNote() {
        return pipelineNote;
    }

}
