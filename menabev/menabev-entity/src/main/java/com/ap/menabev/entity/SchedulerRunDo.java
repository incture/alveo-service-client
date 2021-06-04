package com.ap.menabev.entity;

import java.util.Date;
import java.util.List;

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
@Table(name = "SCHEDULER_RUN")
public class SchedulerRunDo {

	@Id
	@Column(name="SCHEDULER_RUN_ID")
	private String SchedulerRunID;
	
	@Column(name="SCHEDULER_NAME")
	private String SchedulerName;
	
	@Column(name="SWITCHED_ON_BY")
	private String swichtedONby;
	
	@Column(name="DATE_TIME_SWITCHED_ON")
	private Date datetimeSwitchedON;
	
	@Column(name="DATE_TIME_SWITCHED_OFF")
	private Date DatetimeSwitchedOFF;
	
	@Column(name="SWITCHeD_OFF_BY")
	private String swichtedOFFby;
	
	@Column(name="NO_OF_CYCLES")
	private Integer noOfCycles;
	
	@Column(name="SCHEDULER_CONFIG_ID")
	private String schedulerConfigID;
	
}
