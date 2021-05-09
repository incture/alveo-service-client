package com.ap.menabev.dto;


import java.util.UUID;

import lombok.Data;

public @Data class AttachmentDto {
	
	private String attachmentId= UUID.randomUUID().toString();
	private String requestId;
	private String fileName;
	private String fileType;
	private String fileBase64;
	private String createdBy;
	private Long createdAt;
	private String updatedBy;
	private Long updatedAt;
	private Boolean master;

}
