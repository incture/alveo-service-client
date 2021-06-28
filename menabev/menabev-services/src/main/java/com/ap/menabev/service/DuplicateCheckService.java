package com.ap.menabev.service;


import java.util.List;

import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.InvoiceHeaderObjectDto;
import com.ap.menabev.dto.InvoiceItemDto;
import com.ap.menabev.dto.TwoWayMatchInputDto;

public interface DuplicateCheckService {

	InvoiceHeaderObjectDto duplicateCheck(InvoiceHeaderObjectDto dto);

	InvoiceHeaderObjectDto vendorCheck(InvoiceHeaderObjectDto dto);

	InvoiceHeaderDto determineHeaderStatus(InvoiceHeaderDto dto);

	InvoiceItemDto twoWayMatch(TwoWayMatchInputDto dto);
}
