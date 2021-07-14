package com.ap.menabev.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AddPoInputDto {
	
//	i. RequestId
//
//	ii. InvoiceObject { Header, Item, ItemAccountAssignment, CostAllocation} * Check UI Data model in SPRINT 2 DOCS - APInvoiceObjectUI Data Model
//
//	iii. Array of Purchase Order Numbers
//
//	1. {
//
//	2. {“5463789”, “F”}, “”
//
//	3. {“7627289” , “F”} “”
//
//	4. ]
	
	@Getter
	@Setter
	@ToString
	public static class PurchaseOrder{
		String documentNumber;
		String documentCategory;
	}
	private String requestId;
	private InvoiceHeaderDto invoiceHeader;
	List<PurchaseOrder> purchaseOrder;
	private List<PurchaseDocumentHeaderDto> purchaseDocumentHeader;
	
}
