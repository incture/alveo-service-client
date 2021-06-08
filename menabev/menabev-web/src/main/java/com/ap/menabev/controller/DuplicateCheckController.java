package com.ap.menabev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.InvoiceHeaderObjectInputDto;
import com.ap.menabev.dto.InvoiceHeaderObjectOutputDto;
import com.ap.menabev.service.DuplicateCheckService;

@RestController
@RequestMapping("/duplicateCheck")
public class DuplicateCheckController {

	@Autowired
	DuplicateCheckService duplicateCheckService;
	
	@PostMapping("/getDuplicatestatus")
	InvoiceHeaderObjectOutputDto duplicateCheck(@RequestBody InvoiceHeaderObjectInputDto dto){
		return duplicateCheckService.duplicateCheck(dto);
	}
	
}
