package com.ap.menabev.service;

import java.util.List;

import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.SchedulerLogDto;

public interface SchedulerLogService {

	
	List<SchedulerLogDto> getAll();
	ResponseDto saveOrUpdate(SchedulerLogDto dto);
	
}
