package com.ap.menabev.dto;



import java.util.Date;
import java.util.UUID;

import lombok.Data;


public @Data class NonPoTemplateDto { 
	private String uuid = UUID.randomUUID().toString();
	private String templateId;
	private String accClerkId;
	private String basecoderId;
	private String vendorId;
	private String templateName;
	private String createdBy;
	private Date createdAt;
	private String updatedBy;
	private Date updatedAt;
	private String accountNo;
}
