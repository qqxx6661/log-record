package cn.monitor4all.logRecord.bean;


import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class LogDTO {

	/**
	 * 日志唯一ID
	 */
	private String logId;
	/**
	 * 业务ID
	 */
	private String bizId;
	/**
	 * 业务类型
	 */
	private String bizType;
	/**
	 * 方法异常信息
	 */
	private String exception;
	/**
	 * 日志操作时间
	 */
	private Date operateDate;
	/**
	 * 方法是否成功
	 */
	private Boolean success;
	/**
	 * 日志内容
	 */
	private String msg;
	/**
	 * 日志标签
	 */
	private String tag;
	/**
	 * 方法结果
	 */
	private String returnStr;
	/**
	 * 方法执行时间（单位：毫秒）
	 */
	private Long executionTime;
	/**
	 * 额外信息
	 */
	private String extra;
	/**
	 * 操作人ID
	 */
	private String operatorId;
	/**
	 * 实体DIFF列表
	 */
	private List<DiffDTO> diffDTOList;

}