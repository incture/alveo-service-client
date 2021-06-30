package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;

@Data
public class TwoWayMatchInputDto {
//	a. INPUT
//
//	i. ManualVsAuto - MAN, AUTO
//
//	ii. MatchOrUnmatchFlag : M, U
//
//	iii. VendorId
//
//	iv. InvoiceItemObject{}
//
//	v. Fulll PO Deep array format [ with Header, Item, poHistory, PoHistoryTotals]
	
	private String manualVsAuto;
	private String matchOrUnmatchFlag;
	private String vendorId;
	private InvoiceItemDto invoiceItem;
	private PurchaseDocumentItemDto purchaseDocumentItem;
	private List<PurchaseDocumentHeaderDto> purchaseDocumentHeader;
}
