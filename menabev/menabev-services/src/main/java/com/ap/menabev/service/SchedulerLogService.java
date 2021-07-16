package com.ap.menabev.service;

import java.util.List;

import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.SchedulerFilterLogDto;
import com.ap.menabev.dto.SchedulerLogDto;
import com.ap.menabev.dto.SchedulerLogFilterDto;

public interface SchedulerLogService {

	
	List<SchedulerLogDto> getAll();
	ResponseDto saveOrUpdate(SchedulerLogDto dto);
	SchedulerFilterLogDto getFilterLog(SchedulerLogFilterDto dto);
	
}
