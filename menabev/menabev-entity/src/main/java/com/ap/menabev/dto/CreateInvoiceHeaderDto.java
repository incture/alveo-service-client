package com.ap.menabev.dto;

import lombok.Data;

@Data
public class CreateInvoiceHeaderDto {
	
	private InvoiceHeaderDto invoiceHeaderDto;
	private String responseStatus;
	private int status;

}
