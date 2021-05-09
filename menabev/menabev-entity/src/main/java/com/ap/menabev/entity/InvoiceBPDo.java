package com.ap.menabev.entity;



import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Entity
@Table(name = "INVOICE_BP")
@Getter
@Setter
public class InvoiceBPDo 
{
	@Id
	@Column(name = "ID")
	private String id = UUID.randomUUID().toString();
	
	@Column(name = "INVOICE_ID")
	private String invoiceId;
	
	@Column(name = "REQUEST_ID")
	private String requestId;
	
	@Column(name = "PARTNER_ROLE")
	private String partnerRole;
	
	@Column(name = "POSTAL_CODE")
	private String postalCode;
	
	@Column(name = "CITY")
	private String city;
	
	@Column(name = "STREET")
	private String street;
	
	@Column(name = "COUNTRY")
	private String country;
	
	@Column(name = "PARTNER_NUMBER")
	private String partnerNo;
	
	@Column(name = "TELEPHONE")
	private String telephone;
	
	@Column(name = "PARTNER_NAME")
	private String partnerName;
	
	
	

}
