package com.ap.menabev.dto;

import lombok.Data;

@Data
public class CreateInvoiceHeaderChangeDto {
	
	private InvResponseHeaderDto invoiceHeaderDto;
	private String responseStatus;

}
