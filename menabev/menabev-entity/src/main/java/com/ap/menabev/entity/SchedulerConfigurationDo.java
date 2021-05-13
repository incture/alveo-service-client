package com.ap.menabev.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "SCHEDULER_CONFIGURATION")
@ToString
@Getter
@Setter
public class SchedulerConfigurationDo
{

	@Column(name="CREATED_BY")
	private String createdBy;
	
	@Column(name="CREATED_AT")
	private Long createdAt;
	
	@Column(name="UPDATED_BY")
	private String updatedBy;
	
	@Column(name="UPDATED_AT")
	private Long updatedAt;

	@Id
	@Column(name = "SC_ID",nullable=false)
	private String scId;
	
	@Column(name = "CONFIGURATION_ID")
	private String configurationId;
	
	@Column(name="START_DATE")
	private String startDate;
	
	@Column(name="END_DATE")
	private String endDate;
	
	@Column(name="FREQUENCY_NUMBER")
	private Integer frequencyNumber;
	
	@Column(name="FREQUENCY_UNIT")
	private String frequencyUnit;
	
	@Column(name="IS_ACTIVE")
	private Boolean isActive;
	
	@Column(name="ACTION_TYPE")
	private String actionType;


}
