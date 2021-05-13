package com.ap.menabev.service;

import java.util.List;

import com.ap.menabev.dto.ConfigurationCockpitDto;
import com.ap.menabev.dto.ResponseDto;



public interface ConfigurationCockpitService {

	ResponseDto save(ConfigurationCockpitDto  dto);

	ConfigurationCockpitDto get(String version);
	


	ResponseDto delete(String id);

	List<String> getDistinctVersions();
}
