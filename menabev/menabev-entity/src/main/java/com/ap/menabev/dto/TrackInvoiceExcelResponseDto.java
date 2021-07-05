package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackInvoiceExcelResponseDto {

	private String base64;
	private String applicationType;
	private String documentName;
	private String fileAvailability;
}
