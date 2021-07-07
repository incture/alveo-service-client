package com.ap.menabev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.InvoiceHeaderCheckDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.ThreeWayMatchOutputDto;
import com.ap.menabev.service.ValidateInvoiceService;

@RestController
@RequestMapping(value = "/validate")
public class ValidateInvoiceController {
	
	
	
	@Autowired
	ValidateInvoiceService validateInvoiceService;
	@PostMapping("/header")
	public InvoiceHeaderCheckDto invoiceHeaderCheck(@RequestBody InvoiceHeaderCheckDto invoiceHeaderCheckDto){
		return validateInvoiceService.invoiceHeaderCheck(invoiceHeaderCheckDto);
	}
	@PostMapping("/threeWayMatch")
	public ThreeWayMatchOutputDto threeWayMatch(@RequestBody InvoiceHeaderDto invoiceHeaderDto){
		return validateInvoiceService.threeWayMatch(invoiceHeaderDto);
	}
}
