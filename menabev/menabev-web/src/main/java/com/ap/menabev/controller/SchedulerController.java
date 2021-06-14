package com.ap.menabev.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.soap.SOAPException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.service.AutomationService;
import com.ap.menabev.soap.service.Odata;
import com.ap.menabev.soap.service.OdataCall;

@RestController
@RequestMapping(value = "/scheduler")
public class SchedulerController {
	@Autowired
	OdataCall odataCall;
	@Autowired
	AutomationService automationService;

	@GetMapping
	public List<InvoiceHeaderDto> getJSONFromAbbyy() {
		return automationService.downloadFilesFromSFTPABBYYServer();
	}

	@GetMapping("/testOdata")
	ResponseEntity<?> testOdata() throws IOException, URISyntaxException, JAXBException, SOAPException {
		return odataCall.postNonPoItemsToSAP();
	}
//
//	@GetMapping("/t")
//	public ResponseEntity<?> get() throws IOException, URISyntaxException {
//		return Odata.getPaymentTerms();
//	}
}
