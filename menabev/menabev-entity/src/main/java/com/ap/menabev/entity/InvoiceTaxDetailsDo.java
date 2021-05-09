package com.ap.menabev.entity;



import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "INVOICE_TAX_DETAILS")
@ToString
@Getter
@Setter
public @Data class InvoiceTaxDetailsDo 
{
	@Id
	@Column(name = "ID")
	private String id= UUID.randomUUID().toString();
	
	@Column(name = "REQUEST_ID")
	private String requestId;
	
	@Column(name = "INVOICE_ID")
	private String invoiceId;
	
	@Column(name = "TAX_CODE")
	private String taxCode;
	
	@Column(name = "TAX_JURISDICTION")
	private String taxJurisdiction;
	
	@Column(name = "TAX_PER")
	private String taxPer;
	
	@Column(name = "TAX_VALUE")
	private String taxValue;
	
	@Column(name = "CURRENCYof_CODE")
	private String currencyOfCode;
		
	@Column(name="SHIP_TO")
	private String shipTo;
	
	@Column(name="SHIP_FROM")
	private String shipFrom;
	
	@Column(name="PURCHASE_cLASSIFICATION")
	private String purClassification;
	
	@Column(name="VAT_AMOUNT")
	private String vatAmount;
	
	@Column(name="VAT_RATE")
	private String vatRate;
	
	
}
