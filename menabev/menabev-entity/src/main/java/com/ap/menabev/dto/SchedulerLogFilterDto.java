package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SchedulerLogFilterDto {
	private String fromdate;
	private String toDate;
	private Boolean isEmailScheduler;
	private Boolean isOCRScheduler;
	private Boolean isGRNScheduler;
}
