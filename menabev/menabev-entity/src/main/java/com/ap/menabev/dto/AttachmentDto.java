package com.ap.menabev.dto;


import lombok.Data;

public @Data class AttachmentDto {
	
    private String attachmentId ;
	private String requestId;
	private String fileName;
	private String fileType;
	private String fileBase64;
	private String createdBy;
	private Long createdAt;
	private String updatedBy;
	private Long updatedAt;
	private Boolean master;
	private Boolean isPoInvoice;
	private Boolean isDeleted;

}
