package com.ap.menabev.dto;



import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class InvoiceBPDto 
{
	private String id = UUID.randomUUID().toString();
	private String requestId;
	private String invoiceId;
	private String partnerRole;
	private String postalCode;
	private String city;
	private String street;
	private String country;
	private String partnerNo;
	private String telephone;
	private String partnerName;
}
