package com.ap.menabev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.InvoiceOverheadChargesDto;
import com.ap.menabev.service.InvoiceOverheadChargesService;
import com.ap.menabev.dto.ResponseDto;

@RestController
@RequestMapping("/invoiceOverheadCharges")
public class InvoiceOverheadChargesController 
{
	@Autowired
	InvoiceOverheadChargesService overheadChargeService;
	
	@PostMapping("/save")
	public ResponseDto save(@RequestBody InvoiceOverheadChargesDto dto) {
		return overheadChargeService.save(dto);
	}
}
