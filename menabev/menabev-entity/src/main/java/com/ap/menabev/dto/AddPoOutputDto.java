package com.ap.menabev.dto;


import java.util.List;

import lombok.Data;

@Data
public class AddPoOutputDto {

//	i. InvoiceObject {} *-Updated Data after Java Processing
//
//	ii. ReferencePOObjects[] with Header, Item, services, deliveryCosts, history and History Totals.
//
//	iii. Messages []
	
	private InvoiceHeaderDto invoiceObject;
	private List<PurchaseDocumentHeaderDto> referencePo;
	private List<MessageDto> messages;
}
