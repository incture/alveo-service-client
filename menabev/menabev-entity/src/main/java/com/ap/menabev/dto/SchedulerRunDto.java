package com.ap.menabev.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



@Getter
@Setter
@ToString
public class SchedulerRunDto {

    private String SchedulerRunID;
	private String SchedulerName;
	private String swichtedONby;
	private String datetimeSwitchedON;
	private String DatetimeSwitchedOFF;
	private String swichtedOFFby;
	private Integer noOfCycles;
	private String schedulerConfigID;
	
}
