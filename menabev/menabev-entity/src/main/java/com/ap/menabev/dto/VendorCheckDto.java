package com.ap.menabev.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VendorCheckDto {
	private String requestId;
	private String vendorId;
	private String companyCode;
	private String invoiceDate;
	private String invoiceReference;
	private String invoiceAmount;
	private String invoiceStatus;
	
	private List<Messages> messages;
}
