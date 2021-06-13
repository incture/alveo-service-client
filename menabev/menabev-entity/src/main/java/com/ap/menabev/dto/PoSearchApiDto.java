package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PoSearchApiDto {

	private String documentCategory;

	private String documentNumber;

	private String vendorId;

	private String vendorName;

	private String companyCode;

	private String purchaseOrganization;

	private String createdAtRange;

	private String deliveryNoteNumber;
}
