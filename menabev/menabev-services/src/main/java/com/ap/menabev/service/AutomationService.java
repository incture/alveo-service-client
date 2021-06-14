package com.ap.menabev.service;

import com.ap.menabev.dto.ResponseDto;

public interface AutomationService {
	ResponseDto downloadFilesFromSFTPABBYYServer();

	ResponseDto extractInvoiceFromEmail();

	ResponseDto extractInvoiceFromSharedEmailBox();

}
