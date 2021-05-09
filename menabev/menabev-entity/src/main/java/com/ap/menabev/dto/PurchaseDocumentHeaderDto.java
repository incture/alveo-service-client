package com.ap.menabev.dto;



import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class PurchaseDocumentHeaderDto {

	private String purchaseDocumentHeaderId = UUID.randomUUID().toString();
	//private String confirmationText; in ConfirmationItem
	private String documentNumber;
	private String documentCat;
	private String documentType;
	private String companyCode;
	private String status;
	private String vendor;
	private String paymentTerms;
	private String purchaseOrg;
	private String purchaseGroup;
	private String currency;
	private BigDecimal exchangeRate;
	private BigDecimal documentNetValue;
	private Boolean hasDeliveryCost;
	private String deliveryVendor;
	private String poStatus;
	private String poCreatedBy;
	private String createdBy;
	private Date poCreatedDate;	
	private String poNetPrice;
	private String confirmationText;
	private String comment;
	//Po Net Price
	//po Stauts
	//Po_Created By
	//Po created Date 


	
}
