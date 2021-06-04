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
@Table(name = "SCHEDULER_CYCLE")
public class SchedulerCycleDo {
	
	@Column(name ="SCHEDULER_RUN_ID")
	private String schedulerRunID;
	
	@Id
	@Column(name ="SCHEDULER_CYClE_ID")
	private String schedulerCycleID;
	
	@Column(name ="START_DATE_TIME")
	private Date startDateTime;
	
	@Column(name ="END_DATE_Time")
	private Date EndDatetime;
	
	@Column(name ="NO_OF_EMAIL_PICKED")
	private Integer nOfEmailspicked;
	
	@Column(name ="NO_OF_EMAILS_READ_SUCCESSFULLY")
	private Integer noOfEmailsReadSuccessfully;
	
	@Column(name ="NO_OF_ATTACHMENTS")
	private Integer noOfAttachements;
	
	@Column(name ="NO_OF_PDFS")
	private Integer noOfPDFs;
	
	@Column(name ="NO_OF_JSON_FILES")
	private Integer noOfJSONFiles;

}
