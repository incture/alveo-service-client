package com.ap.menabev.dto;

import lombok.Data;

public @Data class AllocationDto {
    private String templateName;
    private String templateId;
    private String accountNumber;
    private String amount;
	public AllocationDto(String templateName,String templateId, String accountNumber) {
		super();
		this.templateName=templateName;
		this.templateId = templateId;
		this.accountNumber = accountNumber;
	}
    
}
