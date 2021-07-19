package com.ap.menabev.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.ap.menabev.dto.ActivityLogDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.InvoiceSubmitDto;

public interface ActivityLogService {
	
	List<ActivityLogDto>  getLogs(String requestId);
	ResponseEntity<?> save(ActivityLogDto dto);
	List<ActivityLogDto> createActivityLogForSubmit(InvoiceHeaderDto invoiceHeaderDto, String actionCode,
			String actionCodeText);
	ActivityLogDto createActivityLogForPoOrNonPo(InvoiceHeaderDto invoiceHeader);
	List<ActivityLogDto> getByRequestId(InvoiceHeaderDto dto);

	

}
