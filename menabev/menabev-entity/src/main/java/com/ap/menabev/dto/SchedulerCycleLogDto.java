package com.ap.menabev.dto;

import java.util.Date;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SchedulerCycleLogDto {

	private String uuId;
	private String runID;
	private String cycleID;
	private String logMsgNo;
	private String logMsgText;
	private Date timestampIST;
	private Date timestampKSA;
}
