package com.ap.menabev.service;


import com.ap.menabev.dto.InvoiceHeaderObjectDto;

public interface DuplicateCheckService {

	InvoiceHeaderObjectDto duplicateCheck(InvoiceHeaderObjectDto dto);

	InvoiceHeaderObjectDto vendorCheck(InvoiceHeaderObjectDto dto);
}
