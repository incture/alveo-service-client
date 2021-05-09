package com.ap.menabev.service;



import java.util.List;

import com.ap.menabev.dto.InvoiceItemAcctAssignmentDto;
import com.ap.menabev.dto.ResponseDto;



public interface InvoiceItemAcctAssignmentService {
	ResponseDto saveInvoiceItemAcctAssignment(List<InvoiceItemAcctAssignmentDto> dto);

	List<InvoiceItemAcctAssignmentDto> get();

	List<InvoiceItemAcctAssignmentDto> get(String requsestId, String itemId);

	ResponseDto save(InvoiceItemAcctAssignmentDto dto);
}
