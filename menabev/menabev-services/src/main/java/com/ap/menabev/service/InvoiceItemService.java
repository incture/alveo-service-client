package com.ap.menabev.service;



import java.util.List;

import com.ap.menabev.dto.InvoiceItemDto;
import com.ap.menabev.dto.ResponseDto;


public interface InvoiceItemService {
	ResponseDto save(InvoiceItemDto dto);
	List<InvoiceItemDto> getAll();
	ResponseDto delete(String id);
	List<InvoiceItemDto> findAllByLimit(int limit, int offset);

	
	
	
}
