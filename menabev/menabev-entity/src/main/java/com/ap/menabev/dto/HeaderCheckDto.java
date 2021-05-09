package com.ap.menabev.dto;





import lombok.Data;

// For Invoice and PurchaseHeaderCheck
public @Data class HeaderCheckDto {
       InvoiceHeaderDashBoardDto invoiceHeader;
       PurchaseDocumentHeaderDto purchaseHeader;
}
