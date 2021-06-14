package com.ap.menabev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.service.AutomationService;
import com.ap.menabev.dto.ResponseDto;

@RestController
@RequestMapping(value = "/scheduler")
public class SchedulerController {
	
	@Autowired
	AutomationService automationService;

	@GetMapping
	public ResponseDto getJSONFromAbbyy() {
		return automationService.downloadFilesFromSFTPABBYYServer();
	}

	
//
//	@GetMapping("/t")
//	public ResponseEntity<?> get() throws IOException, URISyntaxException {
//		return Odata.getPaymentTerms();
//	}
}
