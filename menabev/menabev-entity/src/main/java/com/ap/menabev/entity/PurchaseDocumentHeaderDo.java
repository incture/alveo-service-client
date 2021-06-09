package com.ap.menabev.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="PURCHASE_DOCUMENT_HEADER")
@Getter
@Setter
public class PurchaseDocumentHeaderDo {
	
	
	@Id
	@Column(name="DOCUMENT_NUMBER")
	private String documentNumber;//Po NUMBER
	
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PurchaseDocHeader")
//	@GenericGenerator(name = "PurchaseDocHeader", strategy = "com.incture.ap.sequences.PurchaseDocumentHeaderSequenceGenerator", parameters = {
//			@Parameter(name = InvoiceHeaderSequenceGenerator.INCREMENT_PARAM, value = "1"), @Parameter(name = InvoiceHeaderSequenceGenerator.VALUE_PREFIX_PARAMETER, value = "PDH-"),
//			@Parameter(name = InvoiceHeaderSequenceGenerator.NUMBER_FORMAT_PARAMETER, value = "%06d"),
//			@Parameter(name = InvoiceHeaderSequenceGenerator.SEQUENCE_PARAM,value="PURCHASE_DOCUMENT_HEADER_SEQ")})
	@Column(name="PURCHASE_DOCUMENT_HEADER_ID")
	private String purchaseDocumentHeaderId;
	
	@Column(name="CONFIRMATION_TEXT")
	private String confirmationText;	
	
	@Column(name="DOCUMENT_CAT")
	private String documentCat;
	@Column(name="DOCUMENT_TYPE")
	private String documentType;
	@Column(name="COMPANY_CODE")
	private String companyCode;
	@Column(name="STATUS")
	private String status;
	@Column(name="VENDOR")
	private String vendor;
	@Column(name="PAYMENT_TERMS")
	private String paymentTerms;
	@Column(name="PURCHASE_ORG")
	private String purchaseOrg;
	@Column(name="PURCHASE_GROUP")
	private String purchaseGroup;
	@Column(name="CURRENCY")
	private String currency;
	@Column(name="EXCHANGE_RATE")
	private BigDecimal exchangeRate;
	@Column(name="DOCUMENT_NET_VALUE")
	private BigDecimal documentNetValue;
	
	@Column(name="HAS_DELIVERY_COST")
	private Boolean hasDeliveryCost;
	@Column(name="DELIVERY_VENDOR")
	private String deliveryVendor;
	
	@Column(name="PO_DATE")
	private Date poDate;
	
	@Column(name="PO_STATUS")
	private String poStatus;
	
	@Column(name="PO_CREATED_BY")
	private String poCreatedBy;
	
	@Column(name="CREATED_BY")
	private String createdBy;
	
	@Column(name="PO_CREATED_DATE")
	private Date poCreatedDate;
	
	@Column(name="PO_NET_PRICE")
	private String poNetPrice;
	//PO date
	
	@Column(name="COMMENT")
	private String comment;
	
	
	
	
	
}
