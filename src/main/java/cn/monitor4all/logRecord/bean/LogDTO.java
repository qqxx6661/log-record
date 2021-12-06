package cn.monitor4all.logRecord.bean;


import lombok.Data;

import java.util.Date;

@Data
public class LogDTO {

	private String logId;
	private String bizId;
	private String bizType;
	private String exception;
	private Date operateDate;
	private Boolean success;
	private String msg;
	private String tag;
	private String returnStr;

}