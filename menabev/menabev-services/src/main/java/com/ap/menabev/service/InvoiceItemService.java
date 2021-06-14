package com.ap.menabev.service;



import java.util.List;

import com.ap.menabev.dto.DashBoardDetailsDto;
import com.ap.menabev.dto.InvoiceItemDashBoardDto;
import com.ap.menabev.dto.InvoiceItemDto;
import com.ap.menabev.dto.ManualMatchingDto;
import com.ap.menabev.dto.ResponseDto;


public interface InvoiceItemService {
	ResponseDto save(InvoiceItemDto dto);

	List<InvoiceItemDto> getAll();

	ResponseDto delete(String id);

	DashBoardDetailsDto saveAll(List<InvoiceItemDto> dtos);

	List<InvoiceItemDto> findAllByLimit(int limit, int offset);

	void update(InvoiceItemDashBoardDto dto);

	DashBoardDetailsDto manualMatch(ManualMatchingDto manualMatchingDto);

	DashBoardDetailsDto unMatch(DashBoardDetailsDto dashBoardDetailsDto);

	// DashBoardDetailsDto grnCalculations(DashBoardDetailsDto
	// dashBoardDetailsDto);
	int updateInvoiceItems(List<InvoiceItemDashBoardDto> itDtoList);
	
	
	
}
