package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class SchedulerConfigurationDto 
{

	private String createdBy;
	
	private Long createdAt;
	
	private String updatedBy;
	
	private Long updatedAt;

	private String scId;
	
	private String configurationId;

	private String startDate;
	
	private String endDate;
	
	private Integer frequencyNumber;
	
	private String frequencyUnit;
	
	private Boolean isActive;
	
	private String actionType;


}
