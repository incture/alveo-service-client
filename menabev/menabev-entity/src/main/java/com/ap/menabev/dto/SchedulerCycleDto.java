package com.ap.menabev.dto;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SchedulerCycleDto {

	private String schedulerRunID;
	private String schedulerCycleID;
	private Date startDateTime;
	private Date EndDatetime;
	private Integer nOfEmailspicked;
	private Integer noOfEmailsReadSuccessfully;
	private Integer noOfAttachements;
	private Integer noOfPDFs;
	private Integer noOfJSONFiles;
	List<SchedulerCycleLogDto> schedulerCycleLogs;
}
