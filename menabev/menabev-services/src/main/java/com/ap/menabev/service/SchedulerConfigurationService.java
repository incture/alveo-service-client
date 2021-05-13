package com.ap.menabev.service;

import java.util.List;

import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.SchedulerConfigurationDto;

public interface SchedulerConfigurationService
{
	ResponseDto save(SchedulerConfigurationDto dto);

	List<SchedulerConfigurationDto> get();

	ResponseDto delete(String id);

}
