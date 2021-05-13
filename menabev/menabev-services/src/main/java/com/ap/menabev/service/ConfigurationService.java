package com.ap.menabev.service;

import java.util.List;

import com.ap.menabev.dto.ConfigurationDto;
import com.ap.menabev.dto.ResponseDto;



public interface ConfigurationService 
{
	ResponseDto save(ConfigurationDto dto);

	List<ConfigurationDto> get();

	ResponseDto delete(Integer id);

}
