package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;

@Data
public class InvoiceSubmitDto {
	private String actionCode;
	private String requestId;
	private String taskId;
	private InvoiceHeaderDto invoice;
    private List<PurchaseDocumentHeaderDto> purchaseOrders;	
    private List<RemediationUser> userList;
    private String message;
    private int  status;
}
