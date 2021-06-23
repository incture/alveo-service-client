package com.ap.menabev.service;

import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.SchedulerResponseDto;
import com.ap.menabev.entity.SchedulerConfigurationDo;

public interface AutomationService {
	ResponseDto downloadFilesFromSFTPABBYYServer();

	ResponseDto extractInvoiceFromEmail();

	ResponseDto extractInvoiceFromSharedEmailBox();
	void extractInvoiceFromSharedEmailBoxInScheduler(SchedulerConfigurationDo entity);

}