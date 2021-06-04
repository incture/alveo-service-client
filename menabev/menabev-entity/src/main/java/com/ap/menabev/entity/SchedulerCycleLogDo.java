package com.ap.menabev.entity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ap.menabev.dto.SchedulerCycleLogDto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name="SCHEDULER_CYCLE_LOG")
public class SchedulerCycleLogDo {

	@Id
	@Column(name="UUID")
	private String uuId;
	
	@Column(name="RUN_ID")
	private String runID;
	
	@Column(name="CYCLE_ID")
	private String cycleID;
	
	@Column(name="LOG_MESSAGE_NO")
	private String logMsgNo;
	
	@Column(name="LOG_MESSAGE_TEXT")
	private String logMsgText;
	
	@Column(name="TIME_STAMP_IST")
	private Date timestampIST;
	
	@Column(name="TIME_STAMP_KSA")
	private Date timestampKSA;
}
