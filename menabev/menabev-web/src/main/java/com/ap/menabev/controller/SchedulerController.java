package com.ap.menabev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.service.AutomationService;

@RestController
@RequestMapping(value = "/scheduler")
public class SchedulerController {
	
	@Autowired
	AutomationService automationService;

	@GetMapping
	public ResponseDto getJSONFromAbbyy() {
		return automationService.downloadFilesFromSFTPABBYYServer(null);
	}

	
//
//	@GetMapping("/t")
//	public ResponseEntity<?> get() throws IOException, URISyntaxException {
//		return Odata.getPaymentTerms();
//	}
}
