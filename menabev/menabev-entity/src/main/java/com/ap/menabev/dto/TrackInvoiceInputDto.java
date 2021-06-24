package com.ap.menabev.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class TrackInvoiceInputDto {
	private String requestId; //0
	private String companyCode;//7
	private List<String> vendorId;//4
	private long invoiceDateFrom;
	private long invoiceDateTo;
	private long requestCreatedOnFrom;
	private long requestCreatedOnTo;
	private String invoiceRefNum;
	private long dueDateFrom;
	private long dueDateTo;
	private String top;
	private String skip;
		
}

