package com.ap.menabev.service;


import com.ap.menabev.dto.InvoiceHeaderObjectInputDto;
import com.ap.menabev.dto.InvoiceHeaderObjectOutputDto;

public interface DuplicateCheckService {

	InvoiceHeaderObjectOutputDto duplicateCheck(InvoiceHeaderObjectInputDto dto);
}
