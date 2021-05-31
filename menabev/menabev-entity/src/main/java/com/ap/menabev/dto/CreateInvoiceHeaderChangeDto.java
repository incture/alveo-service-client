package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;

@Data
public class CreateInvoiceHeaderChangeDto {
	
	private InvoiceHeaderChangeDto invoiceHeaderDto;
	private String responseStatus;

}
