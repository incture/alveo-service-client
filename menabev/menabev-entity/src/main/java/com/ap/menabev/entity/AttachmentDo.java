package com.ap.menabev.entity;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "ATTACHMENT_DOC")
public @Data class AttachmentDo {
	
	@Id
	@Column(name = "ATTACHMENT_ID")
	private String attachmentId ;
	
	@Column(name = "REQUEST_ID",nullable=false)
	private String requestId;
	
	@Column(name="FILE_NAME")
	private String fileName;
	
	@Column(name="FILE_TYPE")
	private String fileType;
	
	@Lob
	@Column(name="FILEBASE64")
	private String fileBase64;
	
	@Column(name="CREATED_BY")
	private String createdBy;
	
	@Column(name="CREATED_AT")
	private Long createdAt;
	
	@Column(name="UPDATED_BY")
	private String updatedBy;
	
	@Column(name="UPDATED_AT")
	private Long updatedAt;
	
	@Column(name="MASTER")
	private Boolean master;
	
	@Column(name = "IS_PO_INVOICE")
	private Boolean isPoInvoice;
	
	@Column(name = "IS_DELETED")
	private Boolean isDeleted;

}