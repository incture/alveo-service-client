package com.ap.menabev.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class SchedulerFilterLogDto {
	private String status;
	private String message;
	private String code;
	private List<SchedulerConfigurationDto> schedulerConfigurationDtoList;
	private List<SchedulerLogDto> schedulerLogDtoList;
	
}
