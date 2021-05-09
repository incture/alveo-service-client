package com.ap.menabev.dto;

import java.util.UUID;

import lombok.Data;

public @Data class ReasonForRejectionDto {
	
	private String rejReasonId= UUID.randomUUID().toString();
	private String languageId;
	private String rejectionText;
	private String reasonforRej;

}
