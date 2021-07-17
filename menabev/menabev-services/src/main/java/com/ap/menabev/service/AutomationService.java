package com.ap.menabev.service;

import java.util.List;

import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.SchedulerResponseDto;
import com.ap.menabev.entity.SchedulerConfigurationDo;

public interface AutomationService {
	ResponseDto downloadFilesFromSFTPABBYYServer();

//	ResponseDto extractInvoiceFromEmail();

	ResponseDto extractInvoiceFromSharedEmailBox();
	void extractInvoiceFromSharedEmailBoxInScheduler(SchedulerConfigurationDo entity);
	List<String> getNames();

	ResponseDto downloadFilesFromSFTPABBYYServer(SchedulerConfigurationDo entity);

}
