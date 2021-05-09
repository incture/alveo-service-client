package com.ap.menabev.dto;



import lombok.Data;

public @Data class ManualMatchingDto {
	private InvoiceHeaderDashBoardDto invoiceHeader;
	private InvoiceItemDashBoardDto invoiceItem;
	private PurchaseDocumentItemDto purchaseDocumentItem;
}
