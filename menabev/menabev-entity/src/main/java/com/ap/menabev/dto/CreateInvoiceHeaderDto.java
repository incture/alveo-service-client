package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;

@Data
public class CreateInvoiceHeaderDto {
	
	private InvoiceHeaderDto invoiceHeaderDto;
	private List<InvoiceItemDto>  invoiceItemDto;
	private List<InvoiceItemAcctAssignmentDto> invoiceItemAcctAssignmentDto;
	private List<CostAllocationDto> costAllocationDto;

}
