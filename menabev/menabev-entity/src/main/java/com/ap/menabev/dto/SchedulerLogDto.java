package com.ap.menabev.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SchedulerLogDto {

	SchedulerRunDto schedulerRunDto;
	List<SchedulerCycleDto> schedulerCycle;
	
}
