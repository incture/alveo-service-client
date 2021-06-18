package com.ap.menabev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.InvoiceHeaderObjectDto;
import com.ap.menabev.service.DuplicateCheckService;

@RestController
@RequestMapping("/duplicateCheck")
public class DuplicateCheckController {

	@Autowired
	DuplicateCheckService duplicateCheckService;
	
	@PostMapping("/getDuplicatestatus")
	InvoiceHeaderObjectDto duplicateCheck(@RequestBody InvoiceHeaderObjectDto dto){
		return duplicateCheckService.duplicateCheck(dto);
	}
	
	@PostMapping("/vendorCheck")
	InvoiceHeaderObjectDto vendorCheck(@RequestBody InvoiceHeaderObjectDto dto){
		return duplicateCheckService.vendorCheck(dto);
	}
	
}