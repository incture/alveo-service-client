package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;

@Data
public class InvoiceSubmitDto {
	private String actionCode;
	private  String requestId;
	private InvoiceHeaderDto invoice;
    private List<PurchaseDocumentHeaderDto> purchaseOrders;	
}
